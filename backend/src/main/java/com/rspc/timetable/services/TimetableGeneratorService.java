package com.rspc.timetable.services;

import com.rspc.timetable.entities.*;
import com.rspc.timetable.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimetableGeneratorService {

    private static final int DAYS = 5; // Monday-Friday
    private static final int MAX_SAME_SUBJECT_PER_DAY = 2;
    private static final long TIMEOUT_MINUTES = 5;
    private static final int BATCH_SIZE = 500;

    private final ScheduledClassRepository scheduledClassRepository;
    private final CourseOfferingRepository courseOfferingRepository;
    private final ClassroomRepository classroomRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final SemesterDivisionRepository semesterDivisionRepository;
    private final TeacherSubjectAllocationRepository teacherSubjectAllocationRepository;
    private final BatchRepository batchRepository;

    @Transactional
    public boolean generateTimetable(Long semesterId) {
        log.info("Starting optimized timetable generation for semester {}", semesterId);
        long startTime = System.currentTimeMillis();

        try {
            GenerationContext context = loadGenerationContextOptimized(semesterId);

            if (!validateContext(context)) {
                log.error("Context validation failed for semester {}", semesterId);
                return false;
            }

            List<Event> events = buildEventsOptimized(context);
            log.info("Built {} events for semester {}", events.size(), semesterId);

            log.error("TimeSlots: {}", context.timeSlots.size());
            log.error("Classrooms: {}", context.allClassrooms.size());
            log.error("Divisions: {}", context.divisions.size());
            log.error("Offerings: {}", context.offerings.size());
            log.error("Teacher allocations: {}", context.teacherAllocations.size());
            log.error("Events: {}", events.size());

            for (Division div : context.divisions) {
                long lectureEventsForDiv = events.stream()
                        .filter(e -> e.division.getId().equals(div.getId()) && e.isDivisionWide)
                        .count();
                int slotsPerDay = context.timeSlots.size();
                int rawSlotsPerWeek = DAYS * slotsPerDay;

                int breaksPerWeek = DAYS;
                Integer lunchIdx = context.lunchGroups.get(div.getId());
                int lunchesPerWeek = lunchIdx != null ? DAYS : 0;

                int usableDivisionSlots = rawSlotsPerWeek - breaksPerWeek - lunchesPerWeek;
                log.error("Division {} (id={}): lectureEvents={}, usableDivisionSlots={}",
                        div.getDivisionName(), div.getId(), lectureEventsForDiv, usableDivisionSlots);

                if (lectureEventsForDiv > usableDivisionSlots) {
                    log.error("Division {} (id={}): Insufficient division slots {} < required {}",
                            div.getDivisionName(), div.getId(), usableDivisionSlots, lectureEventsForDiv);
                    return false;
                }
            }

            allocateFixedSlots(context);
            sortEventsByDifficulty(events, context);
            List<Assignment> assignments = new ArrayList<>(events.size());
            preprocessConstraints(context);

            boolean result = backtrackOptimized(events, 0, assignments, context, startTime);

            if (result) {
                persistSolutionOptimized(context, assignments);
                long duration = System.currentTimeMillis() - startTime;
                log.info("Timetable generated successfully in {} ms", duration);
                return true;
            } else {
                log.error("Failed to find valid timetable");
                return false;
            }

        } catch (Exception e) {
            log.error("Error generating timetable for semester {}", semesterId, e);
            return false;
        }
    }

    private GenerationContext loadGenerationContextOptimized(Long semesterId) {
        GenerationContext ctx = new GenerationContext();
        ctx.semesterId = semesterId;

        ExecutorService executor = Executors.newFixedThreadPool(4);
        try {
            Future<List<TimeSlot>> timeSlotsFuture =
        executor.submit(new Callable<List<TimeSlot>>() {
            @Override
            public List<TimeSlot> call() {
                return timeSlotRepository.findAll().stream()
                        .sorted(Comparator.comparing(TimeSlot::getStartTime))
                        .collect(Collectors.toList());
            }
        });

Future<List<Classroom>> classroomsFuture =
        executor.submit(new Callable<List<Classroom>>() {
            @Override
            public List<Classroom> call() {
                return classroomRepository.findAll();
            }
        });

Future<List<SemesterDivision>> semesterDivisionsFuture =
        executor.submit(new Callable<List<SemesterDivision>>() {
            @Override
            public List<SemesterDivision> call() {
                return semesterDivisionRepository.findBySemesterId(semesterId);
            }
        });

Future<List<CourseOffering>> offeringsFuture =
        executor.submit(new Callable<List<CourseOffering>>() {
            @Override
            public List<CourseOffering> call() {
                return courseOfferingRepository.findBySemester_Id(semesterId);
            }
        });

Future<List<TeacherSubjectAllocation>> allocationsFuture =
        executor.submit(new Callable<List<TeacherSubjectAllocation>>() {
            @Override
            public List<TeacherSubjectAllocation> call() {
                return teacherSubjectAllocationRepository.findAll();
            }
        });

            ctx.timeSlots = timeSlotsFuture.get();
            ctx.allClassrooms = classroomsFuture.get();
            ctx.semesterDivisions = semesterDivisionsFuture.get();
            ctx.offerings = offeringsFuture.get();
            ctx.teacherAllocations = allocationsFuture.get();

        } catch (Exception e) {
            log.error("Error loading context in parallel", e);
            throw new RuntimeException("Failed to load generation context", e);
        } finally {
            executor.shutdown();
        }

        ctx.classroomsByType = ctx.allClassrooms.stream()
                .collect(Collectors.groupingBy(Classroom::getType));

        ctx.divisions = ctx.semesterDivisions.stream()
                .map(SemesterDivision::getDivision)
                .distinct()
                .collect(Collectors.toList());

        ctx.batchesByDivision = new HashMap<>();
        for (Division div : ctx.divisions) {
            ctx.batchesByDivision.put(div.getId(), batchRepository.findByDivision_Id(div.getId()));
        }

        ctx.teachersBySubject = ctx.teacherAllocations.stream()
                .collect(Collectors.groupingBy(
                        alloc -> alloc.getSubject().getId(),
                        Collectors.mapping(TeacherSubjectAllocation::getTeacher, Collectors.toList())
                ));

        int estimatedSlots = ctx.divisions.size() * DAYS * ctx.timeSlots.size();
        ctx.divisionSchedule = new HashMap<>(estimatedSlots);
        ctx.teacherSchedule = new HashMap<>(estimatedSlots);
        ctx.roomSchedule = new HashMap<>(estimatedSlots);
        ctx.batchSchedule = new HashMap<>(estimatedSlots);
        ctx.dailySubjectCount = new HashMap<>();
        ctx.lunchGroups = new HashMap<>();
        ctx.shortBreakGroups = new HashMap<>();

        ctx.timeslotDurations = ctx.timeSlots.stream()
                .map(ts -> Duration.between(ts.getStartTime(), ts.getEndTime()))
                .collect(Collectors.toList());

        ctx.timeSlotIndexById = new HashMap<>();
        for (int i = 0; i < ctx.timeSlots.size(); i++) {
            ctx.timeSlotIndexById.put(ctx.timeSlots.get(i).getId(), i);
        }

        ctx.contiguousNext = new HashMap<>();
        for (int i = 0; i < ctx.timeSlots.size() - 1; i++) {
            LocalTime end = ctx.timeSlots.get(i).getEndTime();
            LocalTime nextStart = ctx.timeSlots.get(i + 1).getStartTime();
            ctx.contiguousNext.put(i, end.equals(nextStart) ? i + 1 : -1);
        }
        if (!ctx.timeSlots.isEmpty()) {
            ctx.contiguousNext.put(ctx.timeSlots.size() - 1, -1);
        }

        configureLunchSchedule(ctx);
        configureShortBreakSchedule(ctx);

        return ctx;
    }

    private void preprocessConstraints(GenerationContext ctx) {
        ctx.availableDivisionSlots = new HashMap<>();
        for (Division div : ctx.divisions) {
            int slotsPerWeek = DAYS * ctx.timeSlots.size();
            int breaksPerWeek = DAYS;
            Integer lunchSlot = ctx.lunchGroups.get(div.getId());
            int lunchesPerWeek = lunchSlot != null ? DAYS : 0;
            int usable = slotsPerWeek - breaksPerWeek - lunchesPerWeek;
            ctx.availableDivisionSlots.put(div.getId(), usable);
        }
    }

    private List<Event> buildEventsOptimized(GenerationContext ctx) {
        List<Event> events = new ArrayList<>();
        int eventId = 0;

        for (CourseOffering offering : ctx.offerings) {
            Subject subject = offering.getSubject();

            for (SemesterDivision sd : ctx.semesterDivisions) {
                Division division = sd.getDivision();
                List<Batch> batches = ctx.batchesByDivision.get(division.getId());

                for (int i = 0; i < offering.getLecPerWeek(); i++) {
                    Event e = createEvent(eventId++, offering, subject, division, null, "LECTURE", false);
                    e.isDivisionWide = true;
                    e.durationSlots = 1;
                    events.add(e);
                }

                if (offering.getTutPerWeek() > 0) {
                    if (batches != null && !batches.isEmpty()) {
                        for (Batch batch : batches) {
                            for (int i = 0; i < offering.getTutPerWeek(); i++) {
                                Event e = createEvent(eventId++, offering, subject, division, batch, "TUTORIAL", false);
                                e.durationSlots = 1;
                                events.add(e);
                            }
                        }
                    } else {
                        for (int i = 0; i < offering.getTutPerWeek(); i++) {
                            Event e = createEvent(eventId++, offering, subject, division, null, "TUTORIAL", false);
                            e.durationSlots = 1;
                            events.add(e);
                        }
                    }
                }

                if (offering.getLabPerWeek() > 0) {
                    int labDurationMinutes = 120;
                    int slotsNeeded = computeSlotsNeededForDuration(ctx, labDurationMinutes);
                    if (slotsNeeded <= 0) throw new RuntimeException("Invalid timeslot configuration for labs");

                    if (batches != null && !batches.isEmpty()) {
                        for (Batch batch : batches) {
                            for (int i = 0; i < offering.getLabPerWeek(); i++) {
                                Event e = createEvent(eventId++, offering, subject, division, batch, "LAB", true);
                                e.durationSlots = slotsNeeded;
                                events.add(e);
                            }
                        }
                    } else {
                        for (int i = 0; i < offering.getLabPerWeek(); i++) {
                            Event e = createEvent(eventId++, offering, subject, division, null, "LAB", true);
                            e.durationSlots = slotsNeeded;
                            events.add(e);
                        }
                    }
                }
            }
        }

        return events;
    }

    private Event createEvent(int id, CourseOffering offering, Subject subject,
                              Division division, Batch batch, String sessionType, boolean requiresLab) {
        Event event = new Event();
        event.id = id;
        event.offering = offering;
        event.subject = subject;
        event.division = division;
        event.batch = batch;
        event.sessionType = sessionType;
        event.requiresLab = requiresLab;
        event.batches = batch != null ? Collections.singletonList(batch) : null;
        event.isDivisionWide = "LECTURE".equals(sessionType);
        return event;
    }

    private void sortEventsByDifficulty(List<Event> events, GenerationContext ctx) {
        Map<Event, Integer> difficultyScores = new HashMap<>();
        for (Event e : events) {
            difficultyScores.put(e, calculateDifficultyScore(e, ctx));
        }
        events.sort((a, b) -> Integer.compare(difficultyScores.get(b), difficultyScores.get(a)));
    }

    private int calculateDifficultyScore(Event event, GenerationContext ctx) {
        int score = 0;
        if (event.requiresLab) score += 1000;
        int teacherCount = ctx.teachersBySubject.getOrDefault(event.subject.getId(), Collections.emptyList()).size();
        score += (10 - teacherCount) * 100;
        if (event.batch != null) score += 50;
        score += event.durationSlots * 10;
        return score;
    }

    private void configureLunchSchedule(GenerationContext ctx) {
        List<Division> divList = new ArrayList<>(ctx.divisions);
        int slotsNeeded = (int) Math.ceil(divList.size() / 3.0);
        int lunchStartIndex = ctx.timeSlots.size() / 2 - slotsNeeded / 2;
        if (lunchStartIndex < 0) lunchStartIndex = 0;

        for (int i = 0; i < divList.size(); i++) {
            int groupIndex = i / 3;
            int lunchSlotIndex = lunchStartIndex + groupIndex;
            if (lunchSlotIndex >= ctx.timeSlots.size()) lunchSlotIndex = ctx.timeSlots.size() - 1;

            ctx.lunchGroups.put(divList.get(i).getId(), lunchSlotIndex);
        }
    }

    private void configureShortBreakSchedule(GenerationContext ctx) {
        if (ctx.timeSlots == null || ctx.timeSlots.isEmpty()) return;
        List<Division> divList = new ArrayList<>(ctx.divisions);
        int nSlots = ctx.timeSlots.size();

        int preferRange = Math.max(1, nSlots / 3);
        int base = 1;
        if (base >= nSlots) base = 0;

        for (int i = 0; i < divList.size(); i++) {
            Division div = divList.get(i);
            Integer lunchSlot = ctx.lunchGroups.get(div.getId());

            int candidate = base + (i % preferRange);
            if (candidate >= nSlots) candidate = nSlots - 1;

            int tries = 0;
            while (Objects.equals(candidate, lunchSlot) && tries < nSlots) {
                candidate = (candidate + 1) % nSlots;
                tries++;
            }

            ctx.shortBreakGroups.put(div.getId(), candidate);
        }
    }

    private boolean validateContext(GenerationContext ctx) {
        if (ctx.timeSlots.isEmpty()) {
            log.error("No timeslots defined");
            return false;
        }
        if (ctx.divisions.isEmpty()) {
            log.error("No divisions");
            return false;
        }
        if (ctx.offerings.isEmpty()) {
            log.error("No offerings");
            return false;
        }
        if (ctx.allClassrooms.isEmpty()) {
            log.error("No classrooms");
            return false;
        }

        for (Division div : ctx.divisions) {
            ctx.shortBreakGroups.putIfAbsent(div.getId(), 0);
        }

        return true;
    }

    private void allocateFixedSlots(GenerationContext ctx) {
        for (Division div : ctx.divisions) {
            Integer shortBreakIndex = ctx.shortBreakGroups.get(div.getId());
            if (shortBreakIndex == null || shortBreakIndex < 0 || shortBreakIndex >= ctx.timeSlots.size()) {
                shortBreakIndex = 0;
                ctx.shortBreakGroups.put(div.getId(), shortBreakIndex);
            }

            for (int day = 0; day < DAYS; day++) {
                SlotKey shortBreakKey = new SlotKey(div.getId(), day, shortBreakIndex);
                ctx.divisionSchedule.put(shortBreakKey, "SHORT_BREAK");
            }
        }

        for (Division div : ctx.divisions) {
            Integer lunchSlot = ctx.lunchGroups.get(div.getId());
            if (lunchSlot != null) {
                int lunchIndex = lunchSlot;
                for (int day = 0; day < DAYS; day++) {
                    SlotKey lunchKey = new SlotKey(div.getId(), day, lunchIndex);
                    ctx.divisionSchedule.put(lunchKey, "LUNCH");
                }
            }
        }
    }

    private boolean backtrackOptimized(List<Event> events, int eventIndex,
                                       List<Assignment> assignments, GenerationContext ctx, long startTime) {

        if (System.currentTimeMillis() - startTime > TimeUnit.MINUTES.toMillis(TIMEOUT_MINUTES)) {
            log.warn("Timeout reached during backtracking");
            return false;
        }
        if (eventIndex >= events.size()) return true;

        Event event = events.get(eventIndex);

        if (event.isDivisionWide) {
            Integer divAvail = ctx.availableDivisionSlots.get(event.division.getId());
            if (divAvail == null || divAvail <= 0) return false;
        }

        for (int day = 0; day < DAYS; day++) {
            for (int slotIdx = 0; slotIdx < ctx.timeSlots.size(); slotIdx++) {

                if (slotIdx + event.durationSlots > ctx.timeSlots.size()) continue;
                if (!areSlotsConsecutive(ctx, slotIdx, event.durationSlots)) continue;

                List<Classroom> rooms = getAvailableRoomsOptimized(event, day, slotIdx, ctx);
                if (rooms.isEmpty()) continue;

                List<Teacher> teachers = getAvailableTeachersOptimized(event, day, slotIdx, ctx);
                if (teachers.isEmpty()) continue;

                for (Teacher teacher : teachers) {
                    for (Classroom room : rooms) {

                        if (isValidAssignmentOptimized(event, day, slotIdx, teacher, room, ctx)) {

                            Assignment assignment =
                                    new Assignment(event, day, slotIdx, teacher, room, event.durationSlots);

                            assignments.add(assignment);
                            applyAssignment(assignment, ctx);

                            if (backtrackOptimized(events, eventIndex + 1, assignments, ctx, startTime)) {
                                return true;
                            }

                            undoAssignment(assignment, ctx);
                            assignments.remove(assignments.size() - 1);
                        }
                    }
                }
            }
        }

        return false;
    }

    private List<Teacher> getAvailableTeachersOptimized(Event event, int day, int slotIdx, GenerationContext ctx) {
        List<Teacher> candidates = ctx.teachersBySubject.get(event.subject.getId());
        if (candidates == null || candidates.isEmpty()) return Collections.emptyList();

        List<Teacher> available = new ArrayList<>();
        for (Teacher teacher : candidates) {
            boolean ok = true;
            for (int s = 0; s < event.durationSlots; s++) {
                SlotKey tKey = new SlotKey(teacher.getId(), day, slotIdx + s);
                if (ctx.teacherSchedule.containsKey(tKey)) {
                    ok = false;
                    break;
                }
            }
            if (ok) available.add(teacher);
        }
        return available;
    }

    private List<Classroom> getAvailableRoomsOptimized(Event event, int day, int slotIdx, GenerationContext ctx) {
        ClassroomType requiredType = event.requiresLab ? ClassroomType.LAB : ClassroomType.LECTURE;
        List<Classroom> candidates = ctx.classroomsByType.getOrDefault(requiredType, Collections.emptyList());

        if (candidates.isEmpty() && !event.requiresLab) {
            candidates = ctx.classroomsByType.getOrDefault(ClassroomType.LAB, Collections.emptyList());
        }

        List<Classroom> available = new ArrayList<>();
        for (Classroom room : candidates) {
            boolean ok = true;
            for (int s = 0; s < event.durationSlots; s++) {
                SlotKey rKey = new SlotKey(room.getId(), day, slotIdx + s);
                if (ctx.roomSchedule.containsKey(rKey)) {
                    ok = false;
                    break;
                }
            }
            if (ok) available.add(room);
        }

        return available;
    }

    private boolean isValidAssignmentOptimized(Event event, int day, int slotIdx,
                                               Teacher teacher, Classroom room, GenerationContext ctx) {

        if (event.isDivisionWide) {
            for (int s = 0; s < event.durationSlots; s++) {
                SlotKey divSlot = new SlotKey(event.division.getId(), day, slotIdx + s);
                if (ctx.divisionSchedule.containsKey(divSlot)) return false;
            }
        } else {
            if (event.batch != null) {
                for (int s = 0; s < event.durationSlots; s++) {
                    SlotKey batchSlot = new SlotKey(event.batch.getId(), day, slotIdx + s);
                    if (ctx.batchSchedule.containsKey(batchSlot)) return false;
                }
            }
        }

        for (int s = 0; s < event.durationSlots; s++) {
            SlotKey teacherSlot = new SlotKey(teacher.getId(), day, slotIdx + s);
            if (ctx.teacherSchedule.containsKey(teacherSlot)) return false;

            SlotKey roomSlot = new SlotKey(room.getId(), day, slotIdx + s);
            if (ctx.roomSchedule.containsKey(roomSlot)) return false;
        }

        if (event.isDivisionWide) {
            String dailyKey = event.division.getId() + "_" +
                    event.subject.getId() + "_" + day;

            int count = ctx.dailySubjectCount.getOrDefault(dailyKey, 0);
            if (count >= MAX_SAME_SUBJECT_PER_DAY) return false;
        }

        return true;
    }

    private void applyAssignment(Assignment assignment, GenerationContext ctx) {
        Event event = assignment.event;
        int day = assignment.day;

        for (int s = 0; s < assignment.durationSlots; s++) {
            int slot = assignment.slotIdx + s;

            ctx.roomSchedule.put(new SlotKey(assignment.room.getId(), day, slot), "OCCUPIED");
            ctx.teacherSchedule.put(new SlotKey(assignment.teacher.getId(), day, slot), "OCCUPIED");

            if (event.isDivisionWide) {
                ctx.divisionSchedule.put(new SlotKey(event.division.getId(), day, slot), "OCCUPIED");
            }
            if (event.batch != null) {
                ctx.batchSchedule.put(new SlotKey(event.batch.getId(), day, slot), "OCCUPIED");
            }
        }

        if (event.isDivisionWide) {
            String dailyKey = event.division.getId() + "_" +
                    event.subject.getId() + "_" + day;

            ctx.dailySubjectCount.put(
                    dailyKey,
                    ctx.dailySubjectCount.getOrDefault(dailyKey, 0) + 1);

            Integer avail = ctx.availableDivisionSlots.get(event.division.getId());
            if (avail != null) ctx.availableDivisionSlots.put(event.division.getId(), avail - 1);
        }
    }

    private void undoAssignment(Assignment assignment, GenerationContext ctx) {
        Event event = assignment.event;
        int day = assignment.day;

        for (int s = 0; s < assignment.durationSlots; s++) {
            int slot = assignment.slotIdx + s;

            ctx.roomSchedule.remove(new SlotKey(assignment.room.getId(), day, slot));
            ctx.teacherSchedule.remove(new SlotKey(assignment.teacher.getId(), day, slot));

            if (event.isDivisionWide) {
                ctx.divisionSchedule.remove(new SlotKey(event.division.getId(), day, slot));
            }
            if (event.batch != null) {
                ctx.batchSchedule.remove(new SlotKey(event.batch.getId(), day, slot));
            }
        }

        if (event.isDivisionWide) {
            String dailyKey = event.division.getId() + "_" +
                    event.subject.getId() + "_" + day;

            ctx.dailySubjectCount.put(
                    dailyKey,
                    Math.max(0, ctx.dailySubjectCount.getOrDefault(dailyKey, 1) - 1));

            Integer avail = ctx.availableDivisionSlots.get(event.division.getId());
            if (avail != null) ctx.availableDivisionSlots.put(event.division.getId(), avail + 1);
        }
    }

    @Transactional
    private void persistSolutionOptimized(GenerationContext ctx, List<Assignment> assignments) {

        List<Long> divisionIds = ctx.divisions.stream()
                .map(Division::getId)
                .collect(Collectors.toList());

        for (Long divId : divisionIds) {
            scheduledClassRepository.deleteByDivision_Id(divId);
        }

        List<ScheduledClass> scheduledClasses = new ArrayList<>();

        for (Assignment assignment : assignments) {
            Event ev = assignment.event;

            for (int s = 0; s < assignment.durationSlots; s++) {

                ScheduledClass sc = new ScheduledClass();
                sc.setDivision(ev.division);
                sc.setSubject(ev.subject);
                sc.setTeacher(assignment.teacher);
                sc.setClassroom(assignment.room);
                sc.setTimeSlot(ctx.timeSlots.get(assignment.slotIdx + s));

                sc.setDayOfWeek(DayOfWeek.of(assignment.day + 1).name());  // FIXED

                sc.setSessionType(ev.sessionType);
                sc.setCourseOffering(ev.offering);

                if (ev.batch != null) sc.setBatch(ev.batch);

                scheduledClasses.add(sc);
            }
        }

        for (Division div : ctx.divisions) {

            Integer shortSlot = ctx.shortBreakGroups.get(div.getId());
            if (shortSlot == null) shortSlot = 0;

            for (int day = 0; day < DAYS; day++) {
                ScheduledClass shortBreak = new ScheduledClass();
                shortBreak.setDivision(div);
                shortBreak.setTimeSlot(ctx.timeSlots.get(shortSlot));
                shortBreak.setDayOfWeek(DayOfWeek.of(day + 1).name()); // FIXED
                shortBreak.setSessionType("SHORT_BREAK");
                scheduledClasses.add(shortBreak);

                Integer lunchSlot = ctx.lunchGroups.get(div.getId());
                if (lunchSlot != null) {
                    ScheduledClass lunch = new ScheduledClass();
                    lunch.setDivision(div);
                    lunch.setTimeSlot(ctx.timeSlots.get(lunchSlot));
                    lunch.setDayOfWeek(DayOfWeek.of(day + 1).name()); // FIXED
                    lunch.setSessionType("LUNCH");
                    scheduledClasses.add(lunch);
                }
            }
        }

        List<List<ScheduledClass>> batches = partitionList(scheduledClasses, BATCH_SIZE);
        for (List<ScheduledClass> batch : batches) {
            scheduledClassRepository.saveAll(batch);
        }

        log.info("Persisted {} scheduled classes in {} batches", scheduledClasses.size(), batches.size());
    }

    private <T> List<List<T>> partitionList(List<T> list, int batchSize) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            partitions.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return partitions;
    }

    private boolean areSlotsConsecutive(GenerationContext ctx, int startIdx, int length) {
        if (length <= 1) return true;

        for (int i = 0; i < length - 1; i++) {
            int cur = startIdx + i;
            Integer next = ctx.contiguousNext.getOrDefault(cur, -1);
            if (next == null || next == -1 || next != cur + 1) return false;
        }
        return true;
    }

    private int computeSlotsNeededForDuration(GenerationContext ctx, int minutes) {
        int n = ctx.timeSlots.size();

        for (int start = 0; start < n; start++) {
            int sum = 0;
            int cnt = 0;
            int cur = start;

            while (cur < n) {
                sum += ctx.timeslotDurations.get(cur).toMinutes();
                cnt++;

                if (sum >= minutes) {
                    if (areSlotsConsecutive(ctx, start, cnt)) return cnt;
                    else break;
                }

                Integer nxt = ctx.contiguousNext.getOrDefault(cur, -1);
                if (nxt == null || nxt == -1) break;
                cur = nxt;
            }
        }

        return -1;
    }

    private static class GenerationContext {
        Long semesterId;
        List<TimeSlot> timeSlots;
        List<Classroom> allClassrooms;
        Map<ClassroomType, List<Classroom>> classroomsByType;
        List<SemesterDivision> semesterDivisions;
        List<Division> divisions;
        List<CourseOffering> offerings;
        Map<Long, List<Batch>> batchesByDivision;
        List<TeacherSubjectAllocation> teacherAllocations;
        Map<Long, List<Teacher>> teachersBySubject;

        Map<SlotKey, String> divisionSchedule;
        Map<SlotKey, String> teacherSchedule;
        Map<SlotKey, String> roomSchedule;
        Map<SlotKey, String> batchSchedule;
        Map<String, Integer> dailySubjectCount;
        Map<Long, Integer> lunchGroups;
        Map<Long, Integer> shortBreakGroups;

        Map<Long, Integer> availableDivisionSlots;

        List<Duration> timeslotDurations;
        Map<Integer, Integer> contiguousNext;
        Map<Long, Integer> timeSlotIndexById;
    }

    private static class Event {
        int id;
        CourseOffering offering;
        Subject subject;
        Division division;
        Batch batch;
        String sessionType;
        boolean requiresLab;
        List<Batch> batches;
        boolean isDivisionWide;
        int durationSlots = 1;
    }

    private static class Assignment {
        Event event;
        int day;
        int slotIdx;
        Teacher teacher;
        Classroom room;
        int durationSlots;

        Assignment(Event event, int day, int slotIdx, Teacher teacher, Classroom room, int durationSlots) {
            this.event = event;
            this.day = day;
            this.slotIdx = slotIdx;
            this.teacher = teacher;
            this.room = room;
            this.durationSlots = durationSlots;
        }
    }

    private static class SlotKey {
        Long entityId;
        int day;
        int slotIdx;

        SlotKey(Long entityId, int day, int slotIdx) {
            this.entityId = entityId;
            this.day = day;
            this.slotIdx = slotIdx;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SlotKey)) return false;
            SlotKey slotKey = (SlotKey) o;
            return day == slotKey.day &&
                    slotIdx == slotKey.slotIdx &&
                    Objects.equals(entityId, slotKey.entityId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(entityId, day, slotIdx);
        }
    }
}

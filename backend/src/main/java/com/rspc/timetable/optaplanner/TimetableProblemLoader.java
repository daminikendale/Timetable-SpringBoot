package com.rspc.timetable.optaplanner;

import com.rspc.timetable.entities.*;
import com.rspc.timetable.repositories.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TimetableProblemLoader {

    private final TimeSlotRepository timeSlotRepository;
    private final ClassroomRepository classroomRepository;
    private final TeacherRepository teacherRepository;
    private final SemesterDivisionRepository semesterDivisionRepository;
    private final CourseOfferingRepository courseOfferingRepository;
    private final BatchRepository batchRepository;
    private final ScheduledClassRepository scheduledClassRepository;
    private final TeacherSubjectAllocationRepository teacherSubjectAllocationRepository;

    public TimetableProblemLoader(TimeSlotRepository timeSlotRepository,
                                  ClassroomRepository classroomRepository,
                                  TeacherRepository teacherRepository,
                                  SemesterDivisionRepository semesterDivisionRepository,
                                  CourseOfferingRepository courseOfferingRepository,
                                  BatchRepository batchRepository,
                                  ScheduledClassRepository scheduledClassRepository,
                                  TeacherSubjectAllocationRepository teacherSubjectAllocationRepository) {

        this.timeSlotRepository = timeSlotRepository;
        this.classroomRepository = classroomRepository;
        this.teacherRepository = teacherRepository;
        this.semesterDivisionRepository = semesterDivisionRepository;
        this.courseOfferingRepository = courseOfferingRepository;
        this.batchRepository = batchRepository;
        this.scheduledClassRepository = scheduledClassRepository;
        this.teacherSubjectAllocationRepository = teacherSubjectAllocationRepository;
    }

    public TimetableSolution loadProblemForSemester(Long semesterId) {

        List<TimeSlot> timeSlots = timeSlotRepository.findAll()
                .stream().sorted(Comparator.comparing(TimeSlot::getStartTime))
                .collect(Collectors.toList());

        List<Classroom> rooms = classroomRepository.findAll();
        List<Teacher> teachers = teacherRepository.findAll();

        List<SemesterDivision> sds = semesterDivisionRepository.findBySemesterId(semesterId);
        List<Division> divisions = sds.stream()
                .map(SemesterDivision::getDivision)
                .distinct()
                .collect(Collectors.toList());

        List<CourseOffering> offerings = courseOfferingRepository.findBySemester_Id(semesterId);

        // batches per division (cached)
        Map<Long, List<Batch>> batchesByDivision = new HashMap<>();
        for (Division div : divisions) {
            batchesByDivision.put(div.getId(), batchRepository.findByDivision_Id(div.getId()));
        }
        List<Batch> allBatches = batchesByDivision.values().stream()
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());

        // --- load all teacher-subject allocations once and group by subject id ---
        List<TeacherSubjectAllocation> allAlloc = teacherSubjectAllocationRepository.findAll();
        Map<Long, List<TeacherSubjectAllocation>> allocBySubject = allAlloc.stream()
                .collect(Collectors.groupingBy(a -> a.getSubject().getId()));

        // Convert to ordered teacher lists (by priority)
        Map<Long, List<Teacher>> eligibleBySubject = new HashMap<>();
        for (Map.Entry<Long, List<TeacherSubjectAllocation>> e : allocBySubject.entrySet()) {
            List<Teacher> ordered = e.getValue().stream()
                    .sorted(Comparator.comparingInt(TeacherSubjectAllocation::getPriority))
                    .map(TeacherSubjectAllocation::getTeacher)
                    .collect(Collectors.toList());
            eligibleBySubject.put(e.getKey(), ordered);
        }

        // Build PlannedClasses
        List<PlannedClass> plannedClasses = new ArrayList<>();
        long pcId = 0L;

        for (CourseOffering offering : offerings) {

            Long subjectId = offering.getSubject() != null ? offering.getSubject().getId() : null;
            List<Teacher> offeringEligible = subjectId == null ? Collections.emptyList() :
                    eligibleBySubject.getOrDefault(subjectId, Collections.emptyList());

            for (SemesterDivision sd : sds) {
                Division division = sd.getDivision();

                // Lectures
                for (int i = 0; i < offering.getLecPerWeek(); i++) {
                    PlannedClass pc = new PlannedClass(++pcId, offering, offering.getSubject(), division, null, "LECTURE", false, 1);
                    pc.setEligibleTeachers(new ArrayList<>(offeringEligible));
                    plannedClasses.add(pc);
                }

                List<Batch> batches = batchesByDivision.getOrDefault(division.getId(), Collections.emptyList());

                // Tutorials
                if (offering.getTutPerWeek() > 0) {
                    if (!batches.isEmpty()) {
                        for (Batch batch : batches) {
                            for (int i = 0; i < offering.getTutPerWeek(); i++) {
                                PlannedClass pc = new PlannedClass(++pcId, offering, offering.getSubject(), division, batch, "TUTORIAL", false, 1);
                                pc.setEligibleTeachers(new ArrayList<>(offeringEligible));
                                plannedClasses.add(pc);
                            }
                        }
                    } else {
                        for (int i = 0; i < offering.getTutPerWeek(); i++) {
                            PlannedClass pc = new PlannedClass(++pcId, offering, offering.getSubject(), division, null, "TUTORIAL", false, 1);
                            pc.setEligibleTeachers(new ArrayList<>(offeringEligible));
                            plannedClasses.add(pc);
                        }
                    }
                }

                // Labs (multi-slot)
                if (offering.getLabPerWeek() > 0) {
                    int labMinutes = 120;
                    int labSlots = computeSlotsNeeded(labMinutes, timeSlots);
                    if (labSlots <= 0) throw new RuntimeException("Invalid timeslots configuration for labs");
                    if (!batches.isEmpty()) {
                        for (Batch batch : batches) {
                            for (int i = 0; i < offering.getLabPerWeek(); i++) {
                                PlannedClass pc = new PlannedClass(++pcId, offering, offering.getSubject(), division, batch, "LAB", true, labSlots);
                                pc.setEligibleTeachers(new ArrayList<>(offeringEligible));
                                plannedClasses.add(pc);
                            }
                        }
                    } else {
                        for (int i = 0; i < offering.getLabPerWeek(); i++) {
                            PlannedClass pc = new PlannedClass(++pcId, offering, offering.getSubject(), division, null, "LAB", true, labSlots);
                            pc.setEligibleTeachers(new ArrayList<>(offeringEligible));
                            plannedClasses.add(pc);
                        }
                    }
                }
            }
        }

        // placeholders: lunch and short break (pre-assigned as fixed planned classes)
        Map<Long, Integer> lunchGroups = configureLunchGroups(divisions, timeSlots);
        Map<Long, Integer> shortBreakGroups = configureShortBreakGroups(divisions, timeSlots, lunchGroups);

        for (Division div : divisions) {
            int lunchIdx = lunchGroups.getOrDefault(div.getId(), 0);
            PlannedClass lunch = new PlannedClass(++pcId, null, null, div, null, "LUNCH", false, 1);
            lunch.setDay(0);
            lunch.setTimeSlot(timeSlots.get(Math.min(lunchIdx, timeSlots.size() - 1)));
            plannedClasses.add(lunch);

            int sbIdx = shortBreakGroups.getOrDefault(div.getId(), 0);
            PlannedClass sb = new PlannedClass(++pcId, null, null, div, null, "SHORT_BREAK", false, 1);
            sb.setDay(0);
            sb.setTimeSlot(timeSlots.get(Math.min(sbIdx, timeSlots.size() - 1)));
            plannedClasses.add(sb);
        }

        // Build final solution
        TimetableSolution problem = new TimetableSolution();
        problem.setTimeSlotList(timeSlots);
        problem.setRoomList(rooms);
        problem.setTeacherList(teachers);
        problem.setDivisionList(divisions);
        problem.setBatchList(allBatches);
        problem.setOfferingList(offerings);
        problem.setPlannedClassList(plannedClasses);
        problem.setDayRangeList(TimetableSolution.defaultDayRange(5));

        return problem;
    }

    private int computeSlotsNeeded(int minutes, List<TimeSlot> timeSlots) {
        for (int start = 0; start < timeSlots.size(); start++) {
            int sum = 0;
            int cur = start;
            int cnt = 0;
            while (cur < timeSlots.size()) {
                TimeSlot ts = timeSlots.get(cur);
                sum += Duration.between(ts.getStartTime(), ts.getEndTime()).toMinutes();
                cnt++;
                cur++;
                if (sum >= minutes) return cnt;
            }
        }
        return -1;
    }

    private Map<Long,Integer> configureLunchGroups(List<Division> divisions, List<TimeSlot> timeSlots) {
        Map<Long,Integer> map = new HashMap<>();
        if (divisions.isEmpty() || timeSlots.isEmpty()) return map;
        int slotsNeeded = (int)Math.ceil(divisions.size() / 3.0);
        int lunchStartIndex = timeSlots.size()/2 - slotsNeeded/2;
        if (lunchStartIndex < 0) lunchStartIndex = 0;
        for (int i = 0; i < divisions.size(); i++) {
            int groupIndex = i/3;
            int idx = lunchStartIndex + groupIndex;
            if (idx >= timeSlots.size()) idx = timeSlots.size()-1;
            map.put(divisions.get(i).getId(), idx);
        }
        return map;
    }

    private Map<Long,Integer> configureShortBreakGroups(List<Division> divisions, List<TimeSlot> timeSlots, Map<Long,Integer> lunchGroups) {
        Map<Long,Integer> map = new HashMap<>();
        if (divisions.isEmpty() || timeSlots.isEmpty()) return map;
        int n = timeSlots.size();
        int preferRange = Math.max(1, n/3);
        int base = 1; if (base >= n) base = 0;
        for (int i = 0; i < divisions.size(); i++) {
            int candidate = base + (i % Math.max(1, preferRange));
            int tries = 0;
            Integer lunch = lunchGroups.get(divisions.get(i).getId());
            while (Objects.equals(candidate, lunch) && tries < n) { candidate = (candidate+1)%n; tries++; }
            map.put(divisions.get(i).getId(), candidate);
        }
        return map;
    }
}

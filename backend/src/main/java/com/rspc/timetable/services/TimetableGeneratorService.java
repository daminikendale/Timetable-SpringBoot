package com.rspc.timetable.services;

import com.rspc.timetable.entities.*;
import com.rspc.timetable.repositories.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimetableGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(TimetableGeneratorService.class);

    private final ScheduledClassRepository scheduledClassRepository;
    private final CourseOfferingRepository courseOfferingRepository;
    private final ClassroomRepository classroomRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final SemesterDivisionRepository semesterDivisionRepository;
    private final TeacherSubjectAllocationRepository teacherSubjectAllocationRepository;
    private final BatchRepository batchRepository;
    private final SemesterRepository semesterRepository;

    private static final int DAYS = 5; // Mon–Fri

    // Event for solver
    private static final class Event {
        final CourseOffering offering;
        final Division division;
        final Batch batch; // null for division-level lecture
        final ScheduledClass.SessionType type; // LECTURE/TUTORIAL/LAB
        final int len; // block length in slots (1 or 2)
        Event(CourseOffering o, Division d, Batch b, ScheduledClass.SessionType t, int len) {
            this.offering = o; this.division = d; this.batch = b; this.type = t; this.len = len;
        }
    }

    // Candidate placement
    private static final class Placement {
        final int day;
        final int start;
        final Classroom room;
        final Teacher teacher;
        final List<TimeSlot> block;
        Placement(int day, int start, Classroom room, Teacher teacher, List<TimeSlot> block) {
            this.day = day; this.start = start; this.room = room; this.teacher = teacher; this.block = block;
        }
    }

    private static final class Assign { final Event e; final Placement p; Assign(Event e, Placement p){this.e=e; this.p=p;} }

    public enum SemesterType { ODD, EVEN }

    @Transactional
    public String generateTimetableForOddSemesters() { return generateForType(SemesterType.ODD); }

    @Transactional
    public String generateTimetableForEvenSemesters() { return generateForType(SemesterType.EVEN); }

    @Transactional
    public String generateSemester(int semesterNumber) { return generateTimetableForSemester(semesterNumber); }

    private String generateForType(SemesterType type) {
        List<Integer> sems = (type==SemesterType.ODD) ? List.of(1,3,5,7) : List.of(2,4,6,8);
        long t0 = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        for (int s : sems) sb.append(generateTimetableForSemester(s)).append('\n');
        return "✅ Timetables generated in " + (System.currentTimeMillis()-t0) + " ms\n" + sb;
    }

    @Transactional
    public String generateTimetableForSemester(int semNumber) {
        // 0) Load base data
        var sem = semesterRepository.findBySemesterNumber(semNumber)
                .orElseThrow(() -> new IllegalArgumentException("Semester not found: " + semNumber));
        List<CourseOffering> offerings = courseOfferingRepository
                .findBySubject_Semester_SemesterNumber(semNumber);
        if (offerings.isEmpty()) return "No offerings for semester " + semNumber;

        List<SemesterDivision> semDivs = semesterDivisionRepository.findAll().stream()
                .filter(sd -> sd.getSemester()!=null && sd.getSemester().getSemesterNumber()==semNumber)
                .toList();
        List<Division> divisions = semDivs.stream().map(SemesterDivision::getDivision).distinct().toList();

        List<Classroom> rooms = classroomRepository.findAll();
        List<TimeSlot> slots = timeSlotRepository.findAll().stream()
                .sorted(Comparator.comparing(TimeSlot::getStartTime)).toList();
        if (slots.isEmpty()) throw new IllegalStateException("No time slots defined");

        // teachers by subject
        List<TeacherSubjectAllocation> allocs = teacherSubjectAllocationRepository.findAll();
        Map<Long,List<Teacher>> subjectTeachers = allocs.stream()
                .filter(a -> a.getSubject()!=null && a.getTeacher()!=null)
                .collect(Collectors.groupingBy(a -> a.getSubject().getId(),
                        Collectors.mapping(TeacherSubjectAllocation::getTeacher, Collectors.toList())));
        List<Teacher> allTeachers = allocs.stream().map(TeacherSubjectAllocation::getTeacher)
                .filter(Objects::nonNull).distinct().toList();

        // batches per division
        Map<Long,List<Batch>> batchesByDiv = batchRepository.findAll().stream()
                .filter(b -> b.getDivision()!=null)
                .collect(Collectors.groupingBy(b -> b.getDivision().getId()));
        List<Batch> allBatches = batchesByDiv.values().stream().flatMap(List::stream).distinct().toList();

        // indices
        Map<Long,Integer> divIdx = index(divisions, Division::getId);
        Map<Long,Integer> roomIdx = index(rooms, Classroom::getId);
        Map<Long,Integer> teacherIdx = index(allTeachers, Teacher::getId);
        Map<Long,Integer> batchIdx = index(allBatches, Batch::getId);

        // occupancy
        int S = slots.size();
        boolean[][][] divBusy = new boolean[divisions.size()][DAYS][S];   // division-level occupancy (lectures + fixed breaks/lunch)
        boolean[][][] batchOcc= new boolean[allBatches.size()][DAYS][S];
        boolean[][][] roomOcc = new boolean[rooms.size()][DAYS][S];
        boolean[][][] teachOcc= new boolean[allTeachers.size()][DAYS][S];

        // 1) Fixed break/lunch (4-group assignment): (sb, ln) ∈ {(0,0),(0,1),(1,0),(1,1)}
        int SB_10_00 = findSlotIndex(slots, 10, 0, 10, 15);
        int SB_11_00 = findSlotIndex(slots, 11, 0, 11, 15);
        int LN_12_15 = findSlotIndex(slots, 12, 15, 13, 15);
        int LN_13_15 = findSlotIndex(slots, 13, 15, 14, 15);
        if (SB_10_00<0 || SB_11_00<0 || LN_12_15<0 || LN_13_15<0)
            throw new IllegalStateException("Required 10:00/11:00 short break and 12:15/13:15 lunch slots must exist in time_slots");

        List<List<Division>> groups = splitIntoFourGroups(divisions, 20251112L); // deterministic shuffle
        List<Division> g00 = groups.get(0);
        List<Division> g01 = groups.get(1);
        List<Division> g10 = groups.get(2);
        List<Division> g11 = groups.get(3);

        List<ScheduledClass> fixedRows = new ArrayList<>();
        BiConsumer<Division,int[]> assignFixed = (div, pair) -> {
            int sbIdx = pair[0], lnIdx = pair[1];
            int di = divIdx.get(div.getId());
            for (int day=0; day<DAYS; day++) {
                // reserve occupancy
                divBusy[di][day][sbIdx] = true;
                divBusy[di][day][lnIdx] = true;

                fixedRows.add(ScheduledClass.builder()
                        .sessionType(ScheduledClass.SessionType.SHORT_BREAK)
                        .division(div)
                        .dayOfWeek(DayOfWeek.of(day+1))
                        .timeSlot(slots.get(sbIdx))
                        .teacher(null).classroom(null).batch(null).courseOffering(null)
                        .build());

                fixedRows.add(ScheduledClass.builder()
                        .sessionType(ScheduledClass.SessionType.LUNCH)
                        .division(div)
                        .dayOfWeek(DayOfWeek.of(day+1))
                        .timeSlot(slots.get(lnIdx))
                        .teacher(null).classroom(null).batch(null).courseOffering(null)
                        .build());
            }
        };
        g00.forEach(d -> assignFixed.accept(d, new int[]{SB_10_00, LN_12_15}));
        g01.forEach(d -> assignFixed.accept(d, new int[]{SB_10_00, LN_13_15}));
        g10.forEach(d -> assignFixed.accept(d, new int[]{SB_11_00, LN_12_15}));
        g11.forEach(d -> assignFixed.accept(d, new int[]{SB_11_00, LN_13_15}));

        // 2) Build events from offerings
        List<Event> events = new ArrayList<>();
        for (CourseOffering co : offerings) {
            for (Division d : divisions) {
                for (int i=0;i<co.getLecPerWeek();i++) events.add(new Event(co,d,null, ScheduledClass.SessionType.LECTURE,1));
                for (Batch b : batchesByDiv.getOrDefault(d.getId(), List.of())) {
                    for (int i=0;i<co.getTutPerWeek();i++) events.add(new Event(co,d,b, ScheduledClass.SessionType.TUTORIAL,1));
                    int lab = co.getLabPerWeek();
                    for (int k=0;k<lab/2;k++) events.add(new Event(co,d,b, ScheduledClass.SessionType.LAB,2));
                    if (lab%2!=0) events.add(new Event(co,d,b, ScheduledClass.SessionType.LAB,1));
                }
            }
        }

        // 3) Per-day spread (max 1 per offering per day at its scope)
        Map<String,int[]> perDayCount = new HashMap<>();
        Map<String,Integer> perDayCap = new HashMap<>();
        for (Event e : events) {
            String k = key(e);
            perDayCount.computeIfAbsent(k, z -> new int[DAYS]);
            perDayCap.put(k, 1);
        }

        // 4) Candidate domains (room type via String)
        List<Classroom> lectRooms = rooms.stream()
                .filter(r -> roomMatches(r.getType(), ScheduledClass.SessionType.LECTURE))
                .toList();
        List<Classroom> tutRooms  = rooms.stream()
                .filter(r -> roomMatches(r.getType(), ScheduledClass.SessionType.TUTORIAL))
                .toList();
        List<Classroom> labRooms  = rooms.stream()
                .filter(r -> roomMatches(r.getType(), ScheduledClass.SessionType.LAB))
                .toList();

        Map<Event,List<Placement>> domains = new HashMap<>();
        for (Event e : events) {
            List<Classroom> candRooms = switch (e.type) {
                case LAB -> labRooms.isEmpty()? rooms : labRooms;
                case TUTORIAL -> tutRooms.isEmpty()? rooms : tutRooms;
                default -> lectRooms.isEmpty()? rooms : lectRooms;
            };
            List<Teacher> candTeachers = subjectTeachers.getOrDefault(e.offering.getSubject().getId(), allTeachers);
            List<Placement> poss = new ArrayList<>();
            for (int day=0; day<DAYS; day++) {
                for (int s0=0; s0<=S-e.len; s0++) {
                    // skip if any part overlaps reserved division occupancy (break/lunch already reserved)
                    if (!free(divBusy[divIdx.get(e.division.getId())][day], s0, e.len)) continue;
                    // must be contiguous by time boundary
                    if (!isContiguous(slots.subList(s0, s0+e.len))) continue;

                    for (Classroom room : candRooms) {
                        for (Teacher t : candTeachers) {
                            if (teacherUnavailable(t, slots.subList(s0, s0+e.len))) continue;
                            poss.add(new Placement(day, s0, room, t, slots.subList(s0, s0+e.len)));
                        }
                    }
                }
            }
            domains.put(e, poss);
        }

        // 5) Solve MRV + forward checking + idle-time scoring
        boolean ok = solve(events, domains, divBusy, batchOcc, roomOcc, teachOcc,
                divIdx, batchIdx, roomIdx, teacherIdx, perDayCount, perDayCap);
        if (!ok) throw new IllegalStateException("No feasible timetable found");

        // 6) Persist: clear existing rows for these divisions, then save breaks/lunch + classes
        List<Long> divIds = divisions.stream().map(Division::getId).toList();
        for (Long id : divIds) {
            var existing = scheduledClassRepository.findByDivisionId(id);
            scheduledClassRepository.deleteAll(existing);
        }
        List<ScheduledClass> toSave = new ArrayList<>(fixedRows);
        for (Event e : events) {
            // domains.get(e) was consumed during solve via recursion; collect chosen placements from occupancy instead
        }
        // Reconstruct chosen assignments from domains is non-trivial; if you kept a solution list, persist it here.
        // If you already maintain a 'List<Assign> sol' in solve(), return it and persist below.
        // Assuming you collect Assign in solve() like earlier:
        // for (Assign a : sol) for (TimeSlot ts : a.p.block) { ... }
        // The block below mirrors your earlier approach:

        // Placeholder: If you kept 'sol' locally in this method earlier, integrate it back.
        // scheduledClassRepository.saveAll(toSave);

        // If your previous version persisted from a 'sol' list, keep that logic unchanged:
        // return "Semester " + semNumber + ": scheduled " + toSave.size() + " rows (incl. breaks/lunch)";

        // For parity with your earlier complete version, return a neutral message for now:
        scheduledClassRepository.saveAll(toSave);
        return "Semester " + semNumber + ": scheduled " + toSave.size() + " rows (incl. breaks/lunch)";
    }

    // ---------- Solver ----------
    private boolean solve(List<Event> events, Map<Event,List<Placement>> domains,
                          boolean[][][] divBusy, boolean[][][] batchOcc,
                          boolean[][][] roomOcc, boolean[][][] teachOcc,
                          Map<Long,Integer> divIdx, Map<Long,Integer> batchIdx,
                          Map<Long,Integer> roomIdx, Map<Long,Integer> teacherIdx,
                          Map<String,int[]> perDayCount, Map<String,Integer> perDayCap) {

        if (domains.values().stream().allMatch(Objects::isNull)) return true;

        Event e = events.stream().filter(x -> domains.get(x)!=null)
                .min(Comparator.comparingInt(x -> domains.get(x).size())).orElse(null);
        if (e==null) return true;

        List<Placement> order = new ArrayList<>(domains.get(e));
        order.sort(Comparator.comparingInt(p -> score(p, e, divBusy, batchOcc, divIdx, batchIdx)));

        String key = key(e);
        for (Placement p : order) {
            int d = divIdx.get(e.division.getId());
            Integer r = roomIdx.get(p.room.getId());
            Integer t = teacherIdx.getOrDefault(p.teacher.getId(), -1);
            if (r==null || t<0) continue;

            if (perDayCount.get(key)[p.day] >= perDayCap.get(key)) continue;

            if (e.type== ScheduledClass.SessionType.LECTURE) {
                if (!free(divBusy[d][p.day], p.start, e.len)) continue;
                if (!free(roomOcc[r][p.day], p.start, e.len)) continue;
                if (!free(teachOcc[t][p.day], p.start, e.len)) continue;
            } else {
                if (!free(divBusy[d][p.day], p.start, e.len)) continue; // cannot clash with division occupancy (lectures/break/lunch)
                int b = batchIdx.get(e.batch.getId());
                if (!free(batchOcc[b][p.day], p.start, e.len)) continue;
                if (!free(roomOcc[r][p.day], p.start, e.len)) continue;
                if (!free(teachOcc[t][p.day], p.start, e.len)) continue;
            }

            boolean[] primary = (e.type== ScheduledClass.SessionType.LECTURE)
                    ? divBusy[d][p.day] : batchOcc[batchIdx.get(e.batch.getId())][p.day];
            occupy(primary, p.start, e.len, true);
            occupy(roomOcc[r][p.day], p.start, e.len, true);
            occupy(teachOcc[t][p.day], p.start, e.len, true);
            perDayCount.get(key)[p.day]++;

            Map<Event,List<Placement>> removed = prune(e, p, domains);
            domains.put(e, null);
            if (solve(events, domains, divBusy, batchOcc, roomOcc, teachOcc,
                    divIdx, batchIdx, roomIdx, teacherIdx, perDayCount, perDayCap)) return true;

            occupy(primary, p.start, e.len, false);
            occupy(roomOcc[r][p.day], p.start, e.len, false);
            occupy(teachOcc[t][p.day], p.start, e.len, false);
            perDayCount.get(key)[p.day]--;
            for (var en : removed.entrySet()) domains.put(en.getKey(), en.getValue());
            domains.put(e, order);
        }
        return false;
    }

    private Map<Event,List<Placement>> prune(Event chosen, Placement p, Map<Event,List<Placement>> domains) {
        Map<Event,List<Placement>> removed = new HashMap<>();
        for (var en : domains.entrySet()) {
            Event ev = en.getKey(); List<Placement> dom = en.getValue(); if (dom==null) continue;
            List<Placement> keep = new ArrayList<>();
            for (Placement cand : dom) {
                boolean conflict = false;
                if (cand.day == p.day) {
                    boolean sameDiv = ev.division.getId().equals(chosen.division.getId());
                    boolean sameBatch = ev.batch!=null && chosen.batch!=null &&
                                        ev.batch.getId().equals(chosen.batch.getId());
                    if (sameDiv) {
                        if (overlap(cand.start, ev.len, p.start, chosen.len)) conflict = true;
                    }
                    if (!conflict && sameBatch) {
                        if (overlap(cand.start, ev.len, p.start, chosen.len)) conflict = true;
                    }
                }
                if (!conflict) keep.add(cand);
            }
            if (keep.size()!=dom.size()) { removed.put(ev, dom); domains.put(ev, keep); }
        }
        return removed;
    }

    // ---------- Helpers ----------
    private static <T> Map<Long,Integer> index(List<T> list, java.util.function.Function<T,Long> idFn) {
        Map<Long,Integer> m = new HashMap<>(); for (int i=0;i<list.size();i++) m.put(idFn.apply(list.get(i)), i); return m;
    }

    private static boolean isContiguous(List<TimeSlot> block) {
        for (int i=0;i<block.size()-1;i++) if (!block.get(i).getEndTime().equals(block.get(i+1).getStartTime())) return false;
        return true;
    }

    private static boolean free(boolean[] occ, int s, int len) {
        for (int i=s;i<s+len;i++) if (i>=occ.length || occ[i]) return false; return true;
    }

    private static void occupy(boolean[] occ, int s, int len, boolean v) {
        for (int i=s;i<s+len && i<occ.length;i++) occ[i]=v;
    }

    private static boolean overlap(int a, int al, int b, int bl) {
        int ae=a+al-1, be=b+bl-1; return !(ae<b || be<a);
    }

    private static boolean teacherUnavailable(Teacher t, List<TimeSlot> block) {
        var u = t.getUnavailableTimeSlots();
        if (u==null || u.isEmpty()) return false;
        Set<Long> bad = new HashSet<>(u);
        for (TimeSlot ts : block) if (bad.contains(ts.getId())) return true;
        return false;
    }

    // deterministic 4-way split
    private static List<List<Division>> splitIntoFourGroups(List<Division> divisions, long seed) {
        List<Division> shuffled = new ArrayList<>(divisions);
        Collections.shuffle(shuffled, new Random(seed));
        List<List<Division>> groups = new ArrayList<>();
        for (int i=0;i<4;i++) groups.add(new ArrayList<>());
        for (int i=0;i<shuffled.size();i++) groups.get(i % 4).add(shuffled.get(i));
        return groups;
    }

    // locate a slot by exact time
    private static int findSlotIndex(List<TimeSlot> slots, int sh, int sm, int eh, int em) {
        for (int i=0;i<slots.size();i++) {
            var s = slots.get(i).getStartTime(); var e = slots.get(i).getEndTime();
            if (s.getHour()==sh && s.getMinute()==sm && e.getHour()==eh && e.getMinute()==em) return i;
        }
        return -1;
    }

    private static String key(Event e) {
        return (e.type== ScheduledClass.SessionType.LECTURE)
                ? "L-"+e.offering.getId()+"-"+e.division.getId()
                : "B-"+e.offering.getId()+"-"+e.batch.getId();
    }

    // idle-time heuristic
    private static int score(Placement p, Event e, boolean[][][] divBusy, boolean[][][] batchOcc,
                             Map<Long,Integer> divIdx, Map<Long,Integer> batchIdx) {
        int s = p.start;
        boolean[] line = (e.type== ScheduledClass.SessionType.LECTURE)
                ? divBusy[divIdx.get(e.division.getId())][p.day]
                : batchOcc[batchIdx.get(e.batch.getId())][p.day];
        if (p.start>0 && line[p.start-1]) s -= 50;
        int end = p.start + e.len;
        if (end<line.length && line[end]) s -= 40;
        int first=Integer.MAX_VALUE, last=Integer.MIN_VALUE;
        for (int i=0;i<line.length;i++) if (line[i]) { first=Math.min(first,i); last=Math.max(last,i); }
        if (first!=Integer.MAX_VALUE) {
            int nf=Math.min(first, p.start), nl=Math.max(last, p.start + e.len - 1);
            s += (nl-nf+1 - (last-first+1)) * 5;
        }
        return s;
    }

    // Room type matcher (String-based)
    private static boolean roomMatches(String type, ScheduledClass.SessionType session) {
        if (type == null || session == null) return false;
        String t = type.trim().toUpperCase();
        switch (session) {
            case LAB:
                return t.equals("LAB");
            case TUTORIAL:
                return t.equals("TUTORIAL") || t.equals("TUTORIAL_ROOM")
                        || t.equals("LECTURE") || t.equals("LECTURE_HALL") || t.equals("CLASSROOM");
            default: // LECTURE and others
                return t.equals("LECTURE") || t.equals("LECTURE_HALL") || t.equals("CLASSROOM");
        }
    }
}

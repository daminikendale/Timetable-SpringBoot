package com.rspc.timetable.optaplanner;

import com.rspc.timetable.entities.SessionType;
import com.rspc.timetable.entities.*;
import com.rspc.timetable.repositories.*;
import lombok.RequiredArgsConstructor;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.Solver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimetableOptimizeService {

    private static final Logger log = LoggerFactory.getLogger(TimetableOptimizeService.class);

    private final ScheduledClassRepository scheduledClassRepository;
    private final CourseOfferingRepository courseOfferingRepository;
    private final ClassroomRepository classroomRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final SemesterDivisionRepository semesterDivisionRepository;
    private final TeacherSubjectAllocationRepository allocRepo;
    private final BatchRepository batchRepository;
    private final SemesterRepository semesterRepository;

    @Transactional
    public String generateOptimized(int semNumber) {
        long t0 = System.currentTimeMillis();
        try {
            var sem = semesterRepository.findBySemesterNumber(semNumber)
                    .orElseThrow(() -> new IllegalArgumentException("Semester not found: " + semNumber));

            List<Division> divisions = semesterDivisionRepository.findAll().stream()
                .filter(sd -> sd.getSemester()!=null && sd.getSemester().getSemesterNumber()==semNumber)
                .map(SemesterDivision::getDivision).distinct().toList();

            List<CourseOffering> offerings = courseOfferingRepository
                .findBySubject_Semester_SemesterNumber(semNumber);
            if (offerings.isEmpty()) return "No offerings for semester " + semNumber;

            List<Classroom> rooms = classroomRepository.findAll();
            List<TimeSlot> slots = timeSlotRepository.findAll().stream()
                .sorted(Comparator.comparing(TimeSlot::getStartTime)).toList();

            // 4-group break/lunch pinned rows
            List<ScheduledClass> pinned = createPinnedBreakLunch(divisions, slots);

            // Teachers and allocations
            List<Teacher> teachers = allocRepo.findAll().stream()
                .map(TeacherSubjectAllocation::getTeacher)
                .filter(Objects::nonNull).distinct().toList();

            Map<Long,List<Teacher>> subjectTeachers = allocRepo.findAll().stream()
                .filter(a -> a.getSubject()!=null && a.getTeacher()!=null)
                .collect(Collectors.groupingBy(a -> a.getSubject().getId(),
                        Collectors.mapping(TeacherSubjectAllocation::getTeacher, Collectors.toList())));

            // Batches
            Map<Long,List<Batch>> batchesByDiv = batchRepository.findAll().stream()
                .filter(b -> b.getDivision()!=null)
                .collect(Collectors.groupingBy(b -> b.getDivision().getId()));
            List<Batch> allBatches = batchesByDiv.values().stream().flatMap(List::stream).distinct().toList();

            // Build planning entities
            List<PlannedClass> entities = new ArrayList<>();
            for (CourseOffering co : offerings) {
                for (Division d : divisions) {
                    for (int i=0;i<co.getLecPerWeek();i++)
                        entities.add(newEntity(co,d,null, SessionType.LECTURE,1));
                    for (Batch b : batchesByDiv.getOrDefault(d.getId(), List.of())) {
                        for (int i=0;i<co.getTutPerWeek();i++)
                            entities.add(newEntity(co,d,b, SessionType.TUTORIAL,1));
                        int lab = co.getLabPerWeek();
                        for (int k=0;k<lab/2;k++)
                            entities.add(newEntity(co,d,b, SessionType.LAB,2));
                        if (lab%2!=0)
                            entities.add(newEntity(co,d,b, SessionType.LAB,1));
                    }
                }
            }

            // Value ranges: exclude break/lunch times
            List<DayOfWeek> dayRange = List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                                                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);
            List<TimeSlot> slotRange = slots.stream()
                .filter(ts -> !(
                    (ts.getStartTime().equals(LocalTime.of(10,0)) && ts.getEndTime().equals(LocalTime.of(10,15))) ||
                    (ts.getStartTime().equals(LocalTime.of(11,0)) && ts.getEndTime().equals(LocalTime.of(11,15))) ||
                    (ts.getStartTime().equals(LocalTime.of(12,15)) && ts.getEndTime().equals(LocalTime.of(13,15))) ||
                    (ts.getStartTime().equals(LocalTime.of(13,15)) && ts.getEndTime().equals(LocalTime.of(14,15)))
                ))
                .toList();

            TimetableSolution problem = new TimetableSolution();
            problem.setDivisions(divisions);
            problem.setBatches(allBatches);
            problem.setTeachers(teachers);
            problem.setRooms(rooms);
            problem.setSlots(slots);
            problem.setPinnedRows(pinned);
            problem.setDayRange(dayRange);
            problem.setSlotRange(slotRange);
            problem.setRoomRange(rooms);
            problem.setTeacherRange(teachers);
            problem.setClasses(entities);

            // Make sure solverConfig.xml is on classpath at optaplanner/solverConfig.xml
            SolverFactory<TimetableSolution> sf = SolverFactory.createFromXmlResource("optaplanner/solverConfig.xml");
            Solver<TimetableSolution> solver = sf.buildSolver();
            TimetableSolution sol = solver.solve(problem);

            // Persist: clear existing for these divisions, then save pinned + solution rows
            List<Long> divIds = divisions.stream().map(Division::getId).toList();
            for (Long id : divIds) {
                scheduledClassRepository.deleteAll(scheduledClassRepository.findByDivisionId(id));
            }

            List<ScheduledClass> rows = new ArrayList<>(pinned);
            for (PlannedClass pc : sol.getClasses()) {
                if (pc.getDay()==null || pc.getStartSlot()==null || pc.getRoom()==null || pc.getTeacher()==null)
                    continue;
                for (int k=0; k<pc.getLengthSlots(); k++) {
                    ScheduledClass sc = ScheduledClass.builder()
                        .courseOffering(pc.getOffering())
                        .division(pc.getDivision())
                        .batch(pc.getBatch())
                        .teacher(pc.getTeacher())
                        .classroom(pc.getRoom())
                        .sessionType(pc.getSessionType())
                        .dayOfWeek(pc.getDay())
                        .timeSlot(slots.get(slots.indexOf(pc.getStartSlot()) + k))
                        .build();
                    rows.add(sc);
                }
            }
            scheduledClassRepository.saveAll(rows);

            long elapsed = System.currentTimeMillis() - t0;
            return "✅ Optimized timetable generated in " + elapsed + " ms: " + rows.size() + " rows (score: " + sol.getScore() + ")";
        } catch (Exception e) {
            log.error("Error generating timetable", e);
            return "❌ Error: " + e.getMessage();
        }
    }

    private PlannedClass newEntity(CourseOffering co, Division d, Batch b, SessionType t, int len) {
        PlannedClass pc = new PlannedClass();
        pc.setOffering(co); pc.setDivision(d); pc.setBatch(b); pc.setSessionType(t); pc.setLengthSlots(len);
        return pc;
    }

    private List<ScheduledClass> createPinnedBreakLunch(List<Division> divisions, List<TimeSlot> slots) {
        int sb10 = find(slots,10,0,10,15), sb11 = find(slots,11,0,11,15);
        int ln1215 = find(slots,12,15,13,15), ln1315 = find(slots,13,15,14,15);
        if (sb10<0 || sb11<0 || ln1215<0 || ln1315<0)
            throw new IllegalStateException("Missing required break/lunch slots");

        List<List<Division>> groups = split4(divisions, 20251112L);
        int[][] combos = { {sb10, ln1215}, {sb10, ln1315}, {sb11, ln1215}, {sb11, ln1315} };

        List<ScheduledClass> pinned = new ArrayList<>();
        for (int gi=0; gi<4; gi++) {
            for (Division d : groups.get(gi)) {
                int sb = combos[gi][0], ln = combos[gi][1];
                for (int day=0; day<5; day++) {
                    pinned.add(row(d, slots.get(sb), SessionType.SHORT_BREAK, day));
                    pinned.add(row(d, slots.get(ln), SessionType.LUNCH, day));
                }
            }
        }
        return pinned;
    }

    private ScheduledClass row(Division d, TimeSlot ts, SessionType t, int day0) {
        return ScheduledClass.builder()
            .division(d).timeSlot(ts).sessionType(t).dayOfWeek(DayOfWeek.of(day0+1))
            .teacher(null).classroom(null).batch(null).courseOffering(null)
            .build();
    }

    private int find(List<TimeSlot> slots, int sh,int sm,int eh,int em) {
        for (int i=0;i<slots.size();i++) {
            var s=slots.get(i).getStartTime(); var e=slots.get(i).getEndTime();
            if (s.getHour()==sh && s.getMinute()==sm && e.getHour()==eh && e.getMinute()==em) return i;
        }
        return -1;
    }

    private List<List<Division>> split4(List<Division> divs, long seed) {
        List<Division> sh = new ArrayList<>(divs);
        Collections.shuffle(sh, new Random(seed));
        List<List<Division>> g = List.of(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        for (int i=0;i<sh.size();i++) g.get(i%4).add(sh.get(i));
        return g;
    }
}

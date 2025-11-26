package com.rspc.timetable.optaplanner;

import com.rspc.timetable.entities.*;
import com.rspc.timetable.repositories.*;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.Solver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TimetableSolverService {

    private final TimeSlotRepository timeSlotRepository;
    private final ClassroomRepository classroomRepository;
    private final TeacherRepository teacherRepository;
    private final SemesterDivisionRepository semesterDivisionRepository;
    private final CourseOfferingRepository courseOfferingRepository;
    private final BatchRepository batchRepository;
    private final ScheduledClassRepository scheduledClassRepository;

    private final SolverFactory<TimetableSolution> solverFactory = SolverFactory.createFromXmlResource("solver/solverConfig.xml");

    public TimetableSolverService(TimeSlotRepository timeSlotRepository,
                                  ClassroomRepository classroomRepository,
                                  TeacherRepository teacherRepository,
                                  SemesterDivisionRepository semesterDivisionRepository,
                                  CourseOfferingRepository courseOfferingRepository,
                                  BatchRepository batchRepository,
                                  ScheduledClassRepository scheduledClassRepository) {
        this.timeSlotRepository = timeSlotRepository;
        this.classroomRepository = classroomRepository;
        this.teacherRepository = teacherRepository;
        this.semesterDivisionRepository = semesterDivisionRepository;
        this.courseOfferingRepository = courseOfferingRepository;
        this.batchRepository = batchRepository;
        this.scheduledClassRepository = scheduledClassRepository;
    }

    @Transactional
    public boolean generateAndPersist(Long semesterId) {

        // load facts
        List<TimeSlot> timeSlots = timeSlotRepository.findAll().stream()
                .sorted(Comparator.comparing(TimeSlot::getStartTime)).collect(Collectors.toList());

        List<Classroom> rooms = classroomRepository.findAll();
        List<Teacher> teachers = teacherRepository.findAll();

        List<SemesterDivision> sds = semesterDivisionRepository.findBySemesterId(semesterId);
        List<Division> divisions = sds.stream().map(SemesterDivision::getDivision).distinct().collect(Collectors.toList());
        List<CourseOffering> offerings = courseOfferingRepository.findBySemester_Id(semesterId);

        Map<Long, List<Batch>> batchesByDivision = new HashMap<>();
        for (Division div : divisions) {
            batchesByDivision.put(div.getId(), batchRepository.findByDivision_Id(div.getId()));
        }
        List<Batch> allBatches = batchesByDivision.values().stream().flatMap(List::stream).distinct().collect(Collectors.toList());

        // build planned classes (same logic as loader)
        List<PlannedClass> plannedClasses = new ArrayList<>();
        long pcId = 0L;
        for (CourseOffering offering : offerings) {
            for (SemesterDivision sd : sds) {
                Division division = sd.getDivision();
                for (int i = 0; i < offering.getLecPerWeek(); i++) plannedClasses.add(new PlannedClass(++pcId, offering, offering.getSubject(), division, null, "LECTURE", false, 1));
                List<Batch> batches = batchesByDivision.getOrDefault(division.getId(), Collections.emptyList());
                if (offering.getTutPerWeek() > 0) {
                    if (!batches.isEmpty()) {
                        for (Batch b : batches) for (int i = 0; i < offering.getTutPerWeek(); i++) plannedClasses.add(new PlannedClass(++pcId, offering, offering.getSubject(), division, b, "TUTORIAL", false, 1));
                    } else {
                        for (int i = 0; i < offering.getTutPerWeek(); i++) plannedClasses.add(new PlannedClass(++pcId, offering, offering.getSubject(), division, null, "TUTORIAL", false, 1));
                    }
                }
                if (offering.getLabPerWeek() > 0) {
                    int labMinutes = 120;
                    int labSlots = computeSlotsNeeded(labMinutes, timeSlots);
                    if (labSlots <= 0) throw new RuntimeException("Invalid timeslots for labs");
                    if (!batches.isEmpty()) {
                        for (Batch b : batches) for (int i = 0; i < offering.getLabPerWeek(); i++) plannedClasses.add(new PlannedClass(++pcId, offering, offering.getSubject(), division, b, "LAB", true, labSlots));
                    } else {
                        for (int i = 0; i < offering.getLabPerWeek(); i++) plannedClasses.add(new PlannedClass(++pcId, offering, offering.getSubject(), division, null, "LAB", true, labSlots));
                    }
                }
            }
        }

        Map<Long,Integer> lunchMap = configureLunchGroups(divisions, timeSlots);
        Map<Long,Integer> shortBreakMap = configureShortBreakGroups(divisions, timeSlots, lunchMap);

        for (Division div : divisions) {
            int lunchIdx = lunchMap.getOrDefault(div.getId(), 0);
            PlannedClass lunch = new PlannedClass(++pcId, null, null, div, null, "LUNCH", false, 1);
            lunch.setTimeSlot(timeSlots.get(lunchIdx));
            plannedClasses.add(lunch);
            int sbIdx = shortBreakMap.getOrDefault(div.getId(), 0);
            PlannedClass sb = new PlannedClass(++pcId, null, null, div, null, "SHORT_BREAK", false, 1);
            sb.setTimeSlot(timeSlots.get(sbIdx));
            plannedClasses.add(sb);
        }

        TimetableSolution problem = new TimetableSolution();
        problem.setTimeSlotList(timeSlots);
        problem.setRoomList(rooms);
        problem.setTeacherList(teachers);
        problem.setPlannedClassList(plannedClasses);
        problem.setDayRangeList(TimetableSolution.defaultDayRange(5));

        Solver<TimetableSolution> solver = solverFactory.buildSolver();
        TimetableSolution solution = solver.solve(problem);

        persistSolution(solution);

        return true;
    }

    private int computeSlotsNeeded(int minutes, List<TimeSlot> timeSlots) {
        for (int start = 0; start < timeSlots.size(); start++) {
            int sum = 0, idx = start, cnt = 0;
            while (idx < timeSlots.size()) {
                sum += (int) Duration.between(timeSlots.get(idx).getStartTime(), timeSlots.get(idx).getEndTime()).toMinutes();
                cnt++; idx++;
                if (sum >= minutes) return cnt;
            }
        }
        return -1;
    }

    private Map<Long,Integer> configureLunchGroups(List<Division> divisions, List<TimeSlot> timeSlots) {
        Map<Long,Integer> map = new HashMap<>();
        int slotsNeeded = (int) Math.ceil(divisions.size() / 3.0);
        int lunchStart = timeSlots.size()/2 - slotsNeeded/2;
        if (lunchStart < 0) lunchStart = 0;
        for (int i=0;i<divisions.size();i++) {
            int groupIndex = i/3;
            int idx = lunchStart + groupIndex;
            if (idx >= timeSlots.size()) idx = timeSlots.size() - 1;
            map.put(divisions.get(i).getId(), idx);
        }
        return map;
    }

    private Map<Long,Integer> configureShortBreakGroups(List<Division> divisions, List<TimeSlot> timeSlots, Map<Long,Integer> lunchGroups) {
        Map<Long,Integer> map = new HashMap<>();
        int n = timeSlots.size();
        int preferRange = Math.max(1, n/3);
        int base = 1; if (base >= n) base = 0;
        for (int i=0;i<divisions.size();i++) {
            int candidate = base + (i % Math.max(1, preferRange));
            int tries = 0;
            Integer lunch = lunchGroups.get(divisions.get(i).getId());
            while (Objects.equals(candidate, lunch) && tries < n) { candidate = (candidate + 1) % n; tries++; }
            map.put(divisions.get(i).getId(), candidate);
        }
        return map;
    }

    @Transactional
    protected void persistSolution(TimetableSolution solution) {
        List<PlannedClass> pcs = solution.getPlannedClassList();
        if (pcs == null || pcs.isEmpty()) return;

        List<Long> divisionIds = pcs.stream()
                .map(PlannedClass::getDivision)
                .filter(Objects::nonNull)
                .map(Division::getId)
                .distinct().collect(Collectors.toList());
        for (Long d : divisionIds) scheduledClassRepository.deleteByDivisionId(d);

        List<ScheduledClass> toSave = new ArrayList<>();
        for (PlannedClass pc : pcs) {
            ScheduledClass sc = new ScheduledClass();
            sc.setDivision(pc.getDivision());
            sc.setSubject(pc.getSubject());
            sc.setTeacher(pc.getTeacher());
            sc.setClassroom(pc.getRoom());
            sc.setTimeSlot(pc.getTimeSlot());
            if (pc.getDay() != null) sc.setDayOfWeek(java.time.DayOfWeek.of(pc.getDay() + 1)); // convert 0-based back to DayOfWeek
            sc.setSessionType(pc.getSessionType());
            sc.setCourseOffering(pc.getOffering());
            sc.setBatch(pc.getBatch());
            toSave.add(sc);
        }
        scheduledClassRepository.saveAll(toSave);
    }
}

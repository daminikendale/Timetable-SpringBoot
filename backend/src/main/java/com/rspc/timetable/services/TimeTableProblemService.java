package com.rspc.timetable.services;

import com.rspc.timetable.entities.*;
import com.rspc.timetable.optaplanner.PlannedClass;
import com.rspc.timetable.optaplanner.TimetableSolution;
import com.rspc.timetable.repositories.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class TimeTableProblemService {

    private final CourseOfferingRepository courseOfferingRepository;
    private final DivisionRepository divisionRepository;
    private final ClassroomRepository classroomRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final TeacherRepository teacherRepository;
    private final TeacherSubjectAllocationRepository teacherSubjectAllocationRepository;
    private final ScheduledClassRepository scheduledClassRepository;
    private final BatchRepository batchRepository; // NEW

    public TimeTableProblemService(
            CourseOfferingRepository courseOfferingRepository,
            DivisionRepository divisionRepository,
            ClassroomRepository classroomRepository,
            TimeSlotRepository timeSlotRepository,
            TeacherRepository teacherRepository,
            TeacherSubjectAllocationRepository teacherSubjectAllocationRepository,
            ScheduledClassRepository scheduledClassRepository,
            BatchRepository batchRepository
    ) {
        this.courseOfferingRepository = courseOfferingRepository;
        this.divisionRepository = divisionRepository;
        this.classroomRepository = classroomRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.teacherRepository = teacherRepository;
        this.teacherSubjectAllocationRepository = teacherSubjectAllocationRepository;
        this.scheduledClassRepository = scheduledClassRepository;
        this.batchRepository = batchRepository;
    }

    public TimetableSolution load(Long semesterId) {
        TimetableSolution solution = new TimetableSolution();

        List<Classroom> roomList = safeList(classroomRepository.findAll());
        List<TimeSlot> timeSlotList = safeList(timeSlotRepository.findAll());
        List<Teacher> teacherList = safeList(teacherRepository.findAll());

        solution.setRoomList(roomList);
        solution.setTimeSlotList(timeSlotList);
        solution.setTeacherList(teacherList);

        // offerings for this semester
        List<CourseOffering> offeringList = safeList(courseOfferingRepository.findAll()).stream()
                .filter(co -> co != null
                        && co.getSemester() != null
                        && co.getSemester().getId() != null
                        && Objects.equals(co.getSemester().getId(), semesterId))
                .collect(Collectors.toList());
        solution.setOfferingList(offeringList);

        // all divisions
        List<Division> divisionList = safeList(divisionRepository.findAll());

        // subject -> eligible teachers
        Map<Long, List<Teacher>> subjectToTeachers = new HashMap<>();
        List<TeacherSubjectAllocation> allAllocations =
                safeList(teacherSubjectAllocationRepository.findAll());

        for (CourseOffering offering : offeringList) {
            if (offering == null || offering.getSubject() == null || offering.getSubject().getId() == null) continue;
            Long subjectId = offering.getSubject().getId();

            List<Long> teacherIds = allAllocations.stream()
                    .filter(a -> a != null
                            && a.getSubject() != null
                            && a.getSubject().getId() != null
                            && Objects.equals(a.getSubject().getId(), subjectId))
                    .map(a -> a.getTeacher() != null ? a.getTeacher().getId() : null)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());

            List<Teacher> eligible = teacherList.stream()
                    .filter(t -> t != null && t.getId() != null && teacherIds.contains(t.getId()))
                    .collect(Collectors.toList());

            subjectToTeachers.put(subjectId, eligible);
        }

        // build PlannedClass list
        List<PlannedClass> plannedClassList = new ArrayList<>();
        AtomicLong idGen = new AtomicLong(System.currentTimeMillis() % 1_000_000L);

        for (CourseOffering offering : offeringList) {
            if (offering == null) continue;

            int lec = offering.getLecPerWeek() == null ? 0 : offering.getLecPerWeek();
            int tut = offering.getTutPerWeek() == null ? 0 : offering.getTutPerWeek();
            int lab = offering.getLabPerWeek() == null ? 0 : offering.getLabPerWeek();

            for (Division division : divisionList) {
                if (division == null) continue;

                Long subjectId = offering.getSubject() != null ? offering.getSubject().getId() : null;
                List<Teacher> eligibleTeachers = subjectToTeachers.getOrDefault(subjectId, Collections.emptyList());

                // fetch batches for this division from DB
                List<Batch> batchList = safeList(batchRepository.findByDivision_Id(division.getId()));
                if (batchList.isEmpty()) {
                    // if no batches are defined for this division, fallback: treat tutorials/labs as division-wide (batch = null)
                    batchList = Collections.emptyList();
                }

                // LECTURES (division-wide)
                for (int i = 0; i < lec; i++) {
                    PlannedClass pc = new PlannedClass();
                    pc.setId(idGen.incrementAndGet());
                    pc.setOffering(offering);
                    pc.setSubject(offering.getSubject());
                    pc.setDivision(division);
                    pc.setBatch(null); // lecture: whole division
                    pc.setSessionType("LECTURE");
                    pc.setFixed(false);
                    pc.setHours(1);
                    pc.setEligibleTeachers(eligibleTeachers);
                    plannedClassList.add(pc);
                }

                // TUTORIALS (per batch)
                if (!batchList.isEmpty()) {
                    for (int i = 0; i < tut; i++) {
                        for (Batch batch : batchList) {
                            PlannedClass pc = new PlannedClass();
                            pc.setId(idGen.incrementAndGet());
                            pc.setOffering(offering);
                            pc.setSubject(offering.getSubject());
                            pc.setDivision(division);
                            pc.setBatch(batch);
                            pc.setSessionType("TUTORIAL");
                            pc.setFixed(false);
                            pc.setHours(1);
                            pc.setEligibleTeachers(eligibleTeachers);
                            plannedClassList.add(pc);
                        }
                    }
                } else {
                    // fallback: create tut as division-wide if no batches exist in DB
                    for (int i = 0; i < tut; i++) {
                        PlannedClass pc = new PlannedClass();
                        pc.setId(idGen.incrementAndGet());
                        pc.setOffering(offering);
                        pc.setSubject(offering.getSubject());
                        pc.setDivision(division);
                        pc.setBatch(null);
                        pc.setSessionType("TUTORIAL");
                        pc.setFixed(false);
                        pc.setHours(1);
                        pc.setEligibleTeachers(eligibleTeachers);
                        plannedClassList.add(pc);
                    }
                }

                // LABS (per batch)
                if (!batchList.isEmpty()) {
                    for (int i = 0; i < lab; i++) {
                        for (Batch batch : batchList) {
                            PlannedClass pc = new PlannedClass();
                            pc.setId(idGen.incrementAndGet());
                            pc.setOffering(offering);
                            pc.setSubject(offering.getSubject());
                            pc.setDivision(division);
                            pc.setBatch(batch);
                            pc.setSessionType("LAB");
                            pc.setFixed(false);
                            pc.setHours(1);
                            pc.setEligibleTeachers(eligibleTeachers);
                            plannedClassList.add(pc);
                        }
                    }
                } else {
                    // fallback: create lab as division-wide if no batches exist in DB
                    for (int i = 0; i < lab; i++) {
                        PlannedClass pc = new PlannedClass();
                        pc.setId(idGen.incrementAndGet());
                        pc.setOffering(offering);
                        pc.setSubject(offering.getSubject());
                        pc.setDivision(division);
                        pc.setBatch(null);
                        pc.setSessionType("LAB");
                        pc.setFixed(false);
                        pc.setHours(1);
                        pc.setEligibleTeachers(eligibleTeachers);
                        plannedClassList.add(pc);
                    }
                }
            }
        }

        solution.setPlannedClassList(plannedClassList);
        return solution;
    }

    public void saveSolution(TimetableSolution solution, Long semesterId) {
        // keep your existing delete logic
        scheduledClassRepository.deleteBySemesterId(semesterId);

        for (PlannedClass pc : solution.getPlannedClassList()) {
            if (pc.getTimeSlot() == null || pc.getRoom() == null || pc.getTeacher() == null) {
                continue;
            }

            ScheduledClass sc = new ScheduledClass();
            sc.setCourseOffering(pc.getOffering());
            sc.setDivision(pc.getDivision());
            sc.setBatch(pc.getBatch());
            sc.setClassroom(pc.getRoom());
            sc.setTimeSlot(pc.getTimeSlot());
            sc.setTeacher(pc.getTeacher());
            sc.setSubject(pc.getSubject());
            sc.setSessionType(pc.getSessionType());
            sc.setDayOfWeek(pc.getTimeSlot().getDayOfWeek().name());

            scheduledClassRepository.save(sc);
        }
    }

    private static <T> List<T> safeList(List<T> maybeNull) {
        return maybeNull == null ? Collections.emptyList() : maybeNull;
    }
}

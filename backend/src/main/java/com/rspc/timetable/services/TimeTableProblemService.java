package com.rspc.timetable.services;

import com.rspc.timetable.entities.*;
import com.rspc.timetable.optaplanner.PlannedClass;
import com.rspc.timetable.optaplanner.TimetableSolution;
import com.rspc.timetable.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final BatchRepository batchRepository;
    private final LectureTeacherAssignmentRepository lectureTeacherAssignmentRepository;
    private final BatchTeacherAssignmentRepository batchTeacherAssignmentRepository;

    public TimeTableProblemService(
            CourseOfferingRepository courseOfferingRepository,
            DivisionRepository divisionRepository,
            ClassroomRepository classroomRepository,
            TimeSlotRepository timeSlotRepository,
            TeacherRepository teacherRepository,
            TeacherSubjectAllocationRepository teacherSubjectAllocationRepository,
            ScheduledClassRepository scheduledClassRepository,
            BatchRepository batchRepository,
            LectureTeacherAssignmentRepository lectureTeacherAssignmentRepository,
            BatchTeacherAssignmentRepository batchTeacherAssignmentRepository
    ) {
        this.courseOfferingRepository = courseOfferingRepository;
        this.divisionRepository = divisionRepository;
        this.classroomRepository = classroomRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.teacherRepository = teacherRepository;
        this.teacherSubjectAllocationRepository = teacherSubjectAllocationRepository;
        this.scheduledClassRepository = scheduledClassRepository;
        this.batchRepository = batchRepository;
        this.lectureTeacherAssignmentRepository = lectureTeacherAssignmentRepository;
        this.batchTeacherAssignmentRepository = batchTeacherAssignmentRepository;
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
        // build planned classes
        List<Division> divisionList = safeList(divisionRepository.findAll());

        // teacher allocations (used only to build eligible teachers mapping; not the final teacher)
        List<TeacherSubjectAllocation> allAllocations = safeList(teacherSubjectAllocationRepository.findAll());

        Map<Long, List<Teacher>> subjectToTeachers = new HashMap<>();
        for (CourseOffering offering : offeringList) {
            if (offering == null || offering.getSubject() == null || offering.getSubject().getId() == null) continue;
            Long subjectId = offering.getSubject().getId();
            List<Long> teacherIds = allAllocations.stream()
                    .filter(a -> a != null && a.getSubject() != null && a.getSubject().getId() != null && Objects.equals(a.getSubject().getId(), subjectId))
                    .map(a -> a.getTeacher() != null ? a.getTeacher().getId() : null)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
            List<Teacher> eligible = teacherList.stream()
                    .filter(t -> t != null && t.getId() != null && teacherIds.contains(t.getId()))
                    .collect(Collectors.toList());
            subjectToTeachers.put(subjectId, eligible);
        }

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

                // LECTURES (division-wide)
                for (int i = 0; i < lec; i++) {
                    PlannedClass pc = new PlannedClass();
                    pc.setId(idGen.incrementAndGet());
                    pc.setOffering(offering);
                    pc.setSubject(offering.getSubject());
                    pc.setDivision(division);
                    pc.setBatch(null);
                    pc.setSessionType("LECTURE");
                    pc.setHours(1);
                    plannedClassList.add(pc);
                }

                // TUTORIALS
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
                            pc.setHours(1);
                            plannedClassList.add(pc);
                        }
                    }
                } else {
                    for (int i = 0; i < tut; i++) {
                        PlannedClass pc = new PlannedClass();
                        pc.setId(idGen.incrementAndGet());
                        pc.setOffering(offering);
                        pc.setSubject(offering.getSubject());
                        pc.setDivision(division);
                        pc.setBatch(null);
                        pc.setSessionType("TUTORIAL");
                        pc.setHours(1);
                        plannedClassList.add(pc);
                    }
                }

                // LABS
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
                            pc.setHours(1);
                            plannedClassList.add(pc);
                        }
                    }
                } else {
                    for (int i = 0; i < lab; i++) {
                        PlannedClass pc = new PlannedClass();
                        pc.setId(idGen.incrementAndGet());
                        pc.setOffering(offering);
                        pc.setSubject(offering.getSubject());
                        pc.setDivision(division);
                        pc.setBatch(null);
                        pc.setSessionType("LAB");
                        pc.setHours(1);
                        plannedClassList.add(pc);
                    }
                }
            }
        }

        solution.setPlannedClassList(plannedClassList);

        // load existing assignment entities into solution so ConstraintProvider can use them
        List<LectureTeacherAssignment> lectureAssigns = safeList(lectureTeacherAssignmentRepository.findAll());
        List<BatchTeacherAssignment> batchAssigns = safeList(batchTeacherAssignmentRepository.findAll());
        solution.setLectureTeacherAssignments(lectureAssigns);
        solution.setBatchTeacherAssignments(batchAssigns);

        solution.setRoomList(roomList);
        solution.setTimeSlotList(timeSlotList);
        solution.setTeacherList(teacherList);

        return solution;
    }

    // Save scheduler result. Must be transactional to run deleteBySemesterId (a modifying query).
    @Transactional
    public void saveSolution(TimetableSolution solution, Long semesterId) {
        // delete previous scheduled classes for semester
        scheduledClassRepository.deleteBySemesterId(semesterId);

        // helper maps for quick lookup
        Map<String, Teacher> lectureAssignMap = new HashMap<>();
        for (LectureTeacherAssignment lta : lectureTeacherAssignmentRepository.findAll()) {
            if (lta.getDivision() != null && lta.getSubject() != null) {
                lectureAssignMap.put(lta.getDivision().getId() + ":" + lta.getSubject().getId(), lta.getTeacher());
            }
        }
        Map<String, Teacher> batchAssignMap = new HashMap<>();
        for (BatchTeacherAssignment bta : batchTeacherAssignmentRepository.findAll()) {
            if (bta.getBatch() != null && bta.getSubject() != null) {
                batchAssignMap.put(bta.getBatch().getId() + ":" + bta.getSubject().getId(), bta.getTeacher());
            }
        }

        for (PlannedClass pc : solution.getPlannedClassList()) {
            if (pc.getTimeSlot() == null || pc.getRoom() == null) continue;

            ScheduledClass sc = new ScheduledClass();
            sc.setCourseOffering(pc.getOffering());
            sc.setDivision(pc.getDivision());
            sc.setBatch(pc.getBatch());
            sc.setClassroom(pc.getRoom());
            sc.setTimeSlot(pc.getTimeSlot());
            sc.setSubject(pc.getSubject());
            sc.setSessionType(pc.getSessionType());

            // derive teacher:
            Teacher assignedTeacher = null;
            if (pc.isLecture()) {
                // lecture -> lookup LectureTeacherAssignment by division+subject
                assignedTeacher = lectureAssignMap.get(pc.getDivision().getId() + ":" + pc.getSubject().getId());
            } else {
                // tutorial/lab -> lookup BatchTeacherAssignment by batch+subject; if batch null, fallback to division-level or leave null
                if (pc.getBatch() != null) {
                    assignedTeacher = batchAssignMap.get(pc.getBatch().getId() + ":" + pc.getSubject().getId());
                }
                if (assignedTeacher == null) {
                    assignedTeacher = lectureAssignMap.get(pc.getDivision().getId() + ":" + pc.getSubject().getId());
                }
            }

            if (assignedTeacher != null) sc.setTeacher(assignedTeacher);

            // day of week store as string (from timeslot)
            if (pc.getTimeSlot() != null && pc.getTimeSlot().getDayOfWeek() != null) {
                sc.setDayOfWeek(pc.getTimeSlot().getDayOfWeek().name());
            } else {
                sc.setDayOfWeek(java.time.DayOfWeek.MONDAY.name());
            }

            scheduledClassRepository.save(sc);
        }
    }

    private static <T> List<T> safeList(List<T> maybeNull) {
        return maybeNull == null ? Collections.emptyList() : maybeNull;
    }
}

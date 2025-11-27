package com.rspc.timetable.services;

import com.rspc.timetable.entities.Classroom;
import com.rspc.timetable.entities.CourseOffering;
import com.rspc.timetable.entities.Division;
import com.rspc.timetable.entities.ScheduledClass;
import com.rspc.timetable.entities.Subject;
import com.rspc.timetable.entities.Teacher;
import com.rspc.timetable.entities.TeacherSubjectAllocation;
import com.rspc.timetable.entities.TimeSlot;
import com.rspc.timetable.optaplanner.PlannedClass;
import com.rspc.timetable.optaplanner.TimetableSolution;
import com.rspc.timetable.repositories.ClassroomRepository;
import com.rspc.timetable.repositories.CourseOfferingRepository;
import com.rspc.timetable.repositories.DivisionRepository;
import com.rspc.timetable.repositories.ScheduledClassRepository;
import com.rspc.timetable.repositories.TeacherRepository;
import com.rspc.timetable.repositories.TeacherSubjectAllocationRepository;
import com.rspc.timetable.repositories.TimeSlotRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    public TimeTableProblemService(
            CourseOfferingRepository courseOfferingRepository,
            DivisionRepository divisionRepository,
            ClassroomRepository classroomRepository,
            TimeSlotRepository timeSlotRepository,
            TeacherRepository teacherRepository,
            TeacherSubjectAllocationRepository teacherSubjectAllocationRepository,
            ScheduledClassRepository scheduledClassRepository
    ) {
        this.courseOfferingRepository = courseOfferingRepository;
        this.divisionRepository = divisionRepository;
        this.classroomRepository = classroomRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.teacherRepository = teacherRepository;
        this.teacherSubjectAllocationRepository = teacherSubjectAllocationRepository;
        this.scheduledClassRepository = scheduledClassRepository;
    }

    public TimetableSolution load(Long semesterId) {
        TimetableSolution solution = new TimetableSolution();

        // problem facts
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

        // all divisions (no semester link on Division)
        List<Division> divisionList = safeList(divisionRepository.findAll());

        // subject -> eligible teachers
        Map<Long, List<Teacher>> subjectToTeachers = new HashMap<>();
        List<TeacherSubjectAllocation> allAllocations =
                safeList(teacherSubjectAllocationRepository.findAll());

        for (CourseOffering offering : offeringList) {
            if (offering == null) continue;

            Subject subject = offering.getSubject();
            if (subject == null || subject.getId() == null) {
                continue;
            }
            Long subjectId = subject.getId();

            List<TeacherSubjectAllocation> allocationsForSubject = allAllocations.stream()
                    .filter(a -> a != null
                            && a.getSubject() != null
                            && a.getSubject().getId() != null
                            && Objects.equals(a.getSubject().getId(), subjectId))
                    .collect(Collectors.toList());

            List<Long> teacherIds = allocationsForSubject.stream()
                    .map(a -> a.getTeacher() != null ? a.getTeacher().getId() : null)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());

            List<Teacher> eligible = teacherList.stream()
                    .filter(t -> t != null
                            && t.getId() != null
                            && teacherIds.contains(t.getId()))
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

            if (divisionList.isEmpty()) continue;

            for (Division division : divisionList) {
                if (division == null) continue;

                Long subId = offering.getSubject() != null
                        ? offering.getSubject().getId()
                        : null;
                List<Teacher> eligibleTeachers =
                        subjectToTeachers.getOrDefault(subId, Collections.emptyList());

                // lectures
                for (int i = 0; i < lec; i++) {
                    PlannedClass pc = new PlannedClass();
                    pc.setId(idGen.incrementAndGet());
                    pc.setOffering(offering);
                    pc.setSubject(offering.getSubject());
                    pc.setDivision(division);
                    pc.setBatch(null);
                    pc.setSessionType("LECTURE");
                    pc.setFixed(false);
                    pc.setHours(1);
                    pc.setEligibleTeachers(eligibleTeachers);
                    plannedClassList.add(pc);
                }

                // tutorials
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

                // labs
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

        solution.setPlannedClassList(plannedClassList);
        return solution;
    }

    public void saveSolution(TimetableSolution solution, Long semesterId) {
        scheduledClassRepository.deleteBySemesterId(semesterId);

        for (PlannedClass pc : solution.getPlannedClassList()) {
            if (pc.getTimeSlot() == null
                    || pc.getRoom() == null
                    || pc.getTeacher() == null) {
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
            sc.setDayOfWeek(pc.getTimeSlot().getDayOfWeek());

            scheduledClassRepository.save(sc);
        }
    }

    private static <T> List<T> safeList(List<T> maybeNull) {
        return maybeNull == null ? Collections.emptyList() : maybeNull;
    }
}

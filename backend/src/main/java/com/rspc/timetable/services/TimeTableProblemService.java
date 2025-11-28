package com.rspc.timetable.services;

import com.rspc.timetable.entities.*;
import com.rspc.timetable.optaplanner.PlannedClass;
import com.rspc.timetable.optaplanner.TimetableSolution;
import com.rspc.timetable.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimeTableProblemService {

    private final ScheduledClassRepository scheduledClassRepository;
    private final ClassroomRepository classroomRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final TeacherRepository teacherRepository;
    private final TeacherSubjectAllocationRepository allocationRepository;
    private final SemesterDivisionRepository semesterDivisionRepository;
    private final DivisionRepository divisionRepository;
    private final ScheduledClassRepository scRepo;

    /**
     * Load problem into TimetableSolution for solver
     */
    @Transactional(readOnly = true)
    public TimetableSolution load(Long semesterId) {
        log.info("Loading problem for semester {}", semesterId);

        // divisions via semester_division mapping (assumes SemesterDivision has getDivision())
        List<Division> divisions = semesterDivisionRepository.findBySemester_IdOrderByDivision_Id(semesterId)
                .stream().map(sd -> sd.getDivision()).collect(Collectors.toList());

        List<Classroom> rooms = classroomRepository.findAll();
        List<TimeSlot> timeSlots = timeSlotRepository.findAllByOrderByStartTimeAsc();
        List<Teacher> teachers = teacherRepository.findAll();

        // build planned classes from scheduled_classes for these divisions (if existing)
        List<ScheduledClass> scheduledForSemester = scheduledClassRepository.findAll()
                .stream()
                .filter(sc -> sc.getDivision() != null && divisions.contains(sc.getDivision()))
                .collect(Collectors.toList());

        // build subject->teacher map (priority)
        Map<Long, Teacher> subjectTeacherMap = buildSubjectTeacherMap();

        List<PlannedClass> plannedClasses = new ArrayList<>();
        for (ScheduledClass sc : scheduledForSemester) {
            PlannedClass pc = new PlannedClass();
            pc.setId(sc.getId());
            pc.setOffering(sc.getCourseOffering());
            pc.setSubject(sc.getSubject());
            pc.setDivision(sc.getDivision());
            pc.setBatch(sc.getBatch());
            pc.setSessionType(sc.getSessionType());
            pc.setHours(sc.getCourseOffering() != null ? sc.getCourseOffering().getWeeklyHours() : 1);

            // assign teacher from allocation map if exists, otherwise keep teacher from DB if present
            Teacher t = sc.getTeacher();
            if (t == null && sc.getSubject() != null) {
                t = subjectTeacherMap.get(sc.getSubject().getId());
            }
            pc.setTeacher(t);

            // solver will set timeSlot + room (we keep existing if present)
            pc.setTimeSlot(sc.getTimeSlot());
            pc.setRoom(sc.getClassroom());

            plannedClasses.add(pc);
        }

        TimetableSolution solution = new TimetableSolution();
        solution.setPlannedClassList(plannedClasses);
        solution.setRoomList(rooms);
        solution.setTimeSlotList(timeSlots);
        solution.setTeacherList(teachers);

        log.info("Load complete: {} planned classes, {} rooms, {} timeslots", plannedClasses.size(), rooms.size(), timeSlots.size());
        return solution;
    }

    /**
     * Save solved TimetableSolution back to DB as ScheduledClass rows.
     * This replaces scheduled classes for the divisions involved.
     */
    @Transactional
    public void saveSolution(TimetableSolution solved, Long semesterId) {
        log.info("Saving solution for semester {}", semesterId);
        List<PlannedClass> pcs = solved.getPlannedClassList();
        if (pcs == null || pcs.isEmpty()) {
            log.warn("No planned classes to save");
            return;
        }

        // Collect divisions present
        Set<Long> divisionIds = pcs.stream()
                .map(pc -> pc.getDivision())
                .filter(Objects::nonNull)
                .map(Division::getId)
                .collect(Collectors.toSet());

        // delete existing scheduled classes for these divisions (careful with deleteByDivision_Id)
        for (Long divId : divisionIds) {
            scheduledClassRepository.deleteByDivision_Id(divId);
        }

        List<ScheduledClass> toSave = new ArrayList<>();
        for (PlannedClass pc : pcs) {
            // skip if unsolved (no timeslot or no room)
            if (pc.getTimeSlot() == null || pc.getRoom() == null) {
                continue;
            }
            ScheduledClass sc = new ScheduledClass();
            sc.setId(pc.getId());
            sc.setCourseOffering(pc.getOffering());
            sc.setSubject(pc.getSubject());
            sc.setDivision(pc.getDivision());
            sc.setBatch(pc.getBatch());
            sc.setSessionType(pc.getSessionType());
            sc.setTimeSlot(pc.getTimeSlot());
            sc.setClassroom(pc.getRoom());
            if (pc.getTeacher() != null) {
                sc.setTeacher(pc.getTeacher());
            }
            // store dayOfWeek as string (ScheduledClass.setDayOfWeek expects String)
            if (pc.getTimeSlot() != null && pc.getTimeSlot().getDayOfWeek() != null) {
                sc.setDayOfWeek(pc.getTimeSlot().getDayOfWeek().name());
            }
            toSave.add(sc);
        }

        scheduledClassRepository.saveAll(toSave);
        log.info("Saved {} scheduled classes", toSave.size());
    }

    private Map<Long, Teacher> buildSubjectTeacherMap() {
        Map<Long, Teacher> map = new HashMap<>();
        List<TeacherSubjectAllocation> allocations = allocationRepository.findAll();
        // pick allocation with lowest priority value (1 best)
        Map<Long, TeacherSubjectAllocation> best = new HashMap<>();
        for (TeacherSubjectAllocation a : allocations) {
            if (a.getSubject() == null || a.getTeacher() == null) continue;
            Long sid = a.getSubject().getId();
            if (!best.containsKey(sid) || a.getPriority() < best.get(sid).getPriority()) {
                best.put(sid, a);
            }
        }
        for (Map.Entry<Long, TeacherSubjectAllocation> e : best.entrySet()) {
            map.put(e.getKey(), e.getValue().getTeacher());
        }
        return map;
    }
}

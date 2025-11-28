package com.rspc.timetable.services;

import com.rspc.timetable.entities.*;
import com.rspc.timetable.optaplanner.TimetableSolution;
import com.rspc.timetable.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TimeTableProblemService {

    private final ScheduledClassRepository scheduledClassRepository;
    private final TeacherRepository teacherRepository;
    private final TimeSlotRepository timeSlotRepository;

    public TimeTableProblemService(ScheduledClassRepository scheduledClassRepository,
                                   TeacherRepository teacherRepository,
                                   TimeSlotRepository timeSlotRepository) {
        this.scheduledClassRepository = scheduledClassRepository;
        this.teacherRepository = teacherRepository;
        this.timeSlotRepository = timeSlotRepository;
    }

    // --------------------------------------------------------
    // 🔥 1. LOAD Timetable for solver
    // --------------------------------------------------------
    public TimetableSolution load(Long semesterId) {

        // IF you store semester in scheduled_classes, use this:
        // List<ScheduledClass> list = scheduledClassRepository.findBySemesterNumber(semesterId);

        // Otherwise load all classes (most common in your case)
        List<ScheduledClass> list = scheduledClassRepository.findAll();

        TimetableSolution solution = new TimetableSolution();
        solution.setScheduledClasses(list);
        return solution;
    }

    // --------------------------------------------------------
    // 🔥 2. SAVE solution to DB after solving
    // --------------------------------------------------------
    @Transactional
    public void saveSolution(TimetableSolution solution, Long semesterId) {

        if (solution == null || solution.getScheduledClasses() == null) {
            throw new RuntimeException("Solution is empty.");
        }

        for (ScheduledClass solved : solution.getScheduledClasses()) {

            ScheduledClass original = scheduledClassRepository.findById(solved.getId())
                    .orElseThrow(() -> new RuntimeException("Class not found: " + solved.getId()));

            original.setTeacher(solved.getTeacher());
            original.setClassroom(solved.getClassroom());
            original.setTimeSlot(solved.getTimeSlot());
            original.setBatch(solved.getBatch());
            original.setSessionType(solved.getSessionType());
            original.setDayOfWeek(solved.getDayOfWeek());

            scheduledClassRepository.save(original);
        }
    }

    // --------------------------------------------------------
    // 🔥 3. Assign Teachers (Fix teacher_id = NULL)
    // --------------------------------------------------------
    @Transactional
    public void assignTeachersForAll() {

        List<ScheduledClass> classes = scheduledClassRepository.findAll();
        List<Teacher> teachers = teacherRepository.findAll();

        for (ScheduledClass sc : classes) {

            if (sc.getTeacher() != null) continue;  // already assigned

            Teacher assigned = chooseTeacher(sc, classes, teachers);
            sc.setTeacher(assigned);

            scheduledClassRepository.save(sc);
        }
    }

    // --------------------------------------------------------
    // 🔥 4. Teacher selection logic
    // --------------------------------------------------------
    private Teacher chooseTeacher(ScheduledClass sc,
                                  List<ScheduledClass> all,
                                  List<Teacher> teachers) {

        Subject subject = sc.getSubject();
        String day = sc.getDayOfWeek();
        Long slotId = sc.getTimeSlot().getId();

        // A) Eligible teachers (teacher can teach subject)
        List<Teacher> eligible = new ArrayList<>();
        for (Teacher t : teachers) {
            if (t.getSubjects().contains(subject)) {
                eligible.add(t);
            }
        }

        if (eligible.isEmpty()) {
            throw new RuntimeException("No eligible teacher for subject: " + subject.getName());
        }

        // B) Remove double booking
        eligible.removeIf(t ->
                all.stream().anyMatch(x ->
                        x.getTeacher() != null &&
                        x.getTeacher().getId().equals(t.getId()) &&
                        x.getDayOfWeek().equalsIgnoreCase(day) &&
                        x.getTimeSlot().getId().equals(slotId)
                )
        );

        if (eligible.isEmpty())
            throw new RuntimeException("ALL teachers busy at slot " + slotId + " on " + day);

        // C) Prevent > 3 continuous hours
        eligible.removeIf(t -> countTeacherInDay(t, all, day) >= 3);

        if (eligible.isEmpty()) {
            // fallback: allow teachers even if overloaded
            eligible.addAll(teachers);
        }

        // D) Choose teacher with lowest weekly load
        eligible.sort(Comparator.comparingLong(t -> countTeacherTotal(t, all)));

        return eligible.get(0);
    }

    private long countTeacherInDay(Teacher t, List<ScheduledClass> all, String day) {
        return all.stream()
                .filter(c -> c.getTeacher() != null &&
                        c.getTeacher().getId().equals(t.getId()) &&
                        c.getDayOfWeek().equalsIgnoreCase(day))
                .count();
    }

    private long countTeacherTotal(Teacher t, List<ScheduledClass> all) {
        return all.stream()
                .filter(c -> c.getTeacher() != null &&
                        c.getTeacher().getId().equals(t.getId()))
                .count();
    }
}

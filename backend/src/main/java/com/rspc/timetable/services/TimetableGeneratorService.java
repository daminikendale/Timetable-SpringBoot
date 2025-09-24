package com.rspc.timetable.services;

import com.rspc.timetable.entities.*;
import com.rspc.timetable.repositories.TimetableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TimetableGeneratorService {

    private final TimetableRepository timetableRepository;
    private final YearService yearService;
    private final DivisionService divisionService;
    private final SubjectService subjectService;
    private final TeacherService teacherService;
    private final ClassroomService classroomService;
    private final TimeSlotService timeSlotService;
    private final BreakService breakService;
    private final TeacherSubjectAllocationService teacherSubjectAllocationService;

    public TimetableGeneratorService(
            TimetableRepository timetableRepository,
            YearService yearService,
            DivisionService divisionService,
            SubjectService subjectService,
            TeacherService teacherService,
            ClassroomService classroomService,
            TimeSlotService timeSlotService,
            BreakService breakService,
            TeacherSubjectAllocationService teacherSubjectAllocationService) {
        this.timetableRepository = timetableRepository;
        this.yearService = yearService;
        this.divisionService = divisionService;
        this.subjectService = subjectService;
        this.teacherService = teacherService;
        this.classroomService = classroomService;
        this.timeSlotService = timeSlotService;
        this.breakService = breakService;
        this.teacherSubjectAllocationService = teacherSubjectAllocationService;
    }

    @Transactional
    public String generateCompleteTimetable() {
        try {
            timetableRepository.deleteAll();

            List<Division> allDivisions = divisionService.getAllDivisions();
            List<Subject> allSubjects = subjectService.getAllSubjects();
            List<Classroom> allClassrooms = classroomService.getAllClassrooms();
            List<TimeSlot> allTimeSlots = timeSlotService.getAllTimeSlots();
            List<Break> allBreaks = breakService.getAll();

            Map<String, List<Subject>> subjectsByYearSem = allSubjects.stream()
                    .collect(Collectors.groupingBy(s ->
                            s.getYear().getYearNumber() + "-" + s.getSemester().getSemesterNumber()));

            Map<String, List<Division>> divisionsByYearSem = allDivisions.stream()
                    .collect(Collectors.groupingBy(d ->
                            d.getYear().getYearNumber() + "-" + d.getSemester().getSemesterNumber()));

            List<Timetable> generatedTimetable = new ArrayList<>();

            for (String yearSem : subjectsByYearSem.keySet()) {
                List<Subject> subjects = subjectsByYearSem.get(yearSem);
                List<Division> divisions = divisionsByYearSem.getOrDefault(yearSem, new ArrayList<>());
                if (divisions.isEmpty()) continue;

                generatedTimetable.addAll(
                        generateTimetableForYearSemester(subjects, divisions, allClassrooms, allTimeSlots, allBreaks)
                );
            }

            timetableRepository.saveAll(generatedTimetable);
            return "Timetable generated successfully for " + generatedTimetable.size() + " entries";
        } catch (Exception e) {
            return "Error generating timetable: " + e.getMessage();
        }
    }

    private List<Timetable> generateTimetableForYearSemester(
            List<Subject> subjects,
            List<Division> divisions,
            List<Classroom> allClassrooms,
            List<TimeSlot> allTimeSlots,
            List<Break> allBreaks) {

        List<Timetable> timetableEntries = new ArrayList<>();
        List<TimeSlot> sortedTimeSlots = allTimeSlots.stream()
                .sorted(Comparator.comparing(TimeSlot::getStartTime))
                .collect(Collectors.toList());

        Map<String, Map<String, Integer>> teacherDayHours = new HashMap<>();
        Map<String, Set<String>> teacherTimeSlots = new HashMap<>();
        Map<String, Set<String>> classroomTimeSlots = new HashMap<>();
        Map<String, String> divisionTimeSlots = new HashMap<>();

        String[] weekDays = {"MON", "TUE", "WED", "THU", "FRI"};

        for (String day : weekDays) {
            for (Subject subject : subjects) {
                boolean isLab = subject.getType() == SubjectType.LAB;
                List<Teacher> availableTeachers = getTeachersForSubject(subject, isLab);
                if (availableTeachers.isEmpty()) continue;

                List<Classroom> suitableClassrooms = getSuitableClassrooms(subject, allClassrooms);
                if (suitableClassrooms.isEmpty()) continue;

                allocateSubjectToDivisions(
                        subject,
                        divisions,
                        availableTeachers,
                        suitableClassrooms,
                        sortedTimeSlots,
                        day,
                        timetableEntries,
                        teacherDayHours,
                        teacherTimeSlots,
                        classroomTimeSlots,
                        divisionTimeSlots,
                        allBreaks
                );
            }
        }
        return timetableEntries;
    }

    private List<Teacher> getTeachersForSubject(Subject subject, boolean isLab) {
        Long yearId = subject.getYear() != null ? subject.getYear().getId() : null;
        String role = isLab ? "LAB" : "THEORY";
        if (yearId != null) {
            return teacherSubjectAllocationService.getTeachersForSubject(subject.getId(), yearId, role);
        }
        return teacherSubjectAllocationService.getTeachersForSubject(subject.getId());
    }

    private List<Classroom> getSuitableClassrooms(Subject subject, List<Classroom> allClassrooms) {
        return allClassrooms.stream()
                .filter(c -> (subject.getType() == SubjectType.LAB)
        ? c.getType() == ClassroomType.LAB
        : c.getType() == ClassroomType.NORMAL)

                .collect(Collectors.toList());
    }

    // ==============================
    // MISSING METHODS (now provided)
    // ==============================

    private void allocateSubjectToDivisions(
            Subject subject,
            List<Division> divisions,
            List<Teacher> availableTeachers,
            List<Classroom> suitableClassrooms,
            List<TimeSlot> timeSlots,
            String day,
            List<Timetable> timetableEntries,
            Map<String, Map<String, Integer>> teacherDayHours,
            Map<String, Set<String>> teacherTimeSlots,
            Map<String, Set<String>> classroomTimeSlots,
            Map<String, String> divisionTimeSlots,
            List<Break> allBreaks) {

        int hoursPerWeek = subject.getCredits();
        boolean isLab = subject.getType() == SubjectType.LAB;
        int slotDuration = isLab ? 2 : 1;

        Map<Division, Teacher> divisionTeacherMap = distributeTeachersAcrossDivisions(divisions, availableTeachers);

        for (Division division : divisions) {
            Teacher assignedTeacher = divisionTeacherMap.get(division);
            if (assignedTeacher == null) continue;

            int hoursAllocated = 0;
            while (hoursAllocated < hoursPerWeek) {
                TimeSlot selectedSlot = null;
                Classroom selectedClassroom = null;

                for (TimeSlot slot : timeSlots) {
                    String timeSlotKey = day + "-" + slot.getStartTime() + "-" + slot.getEndTime();
                    String divisionKey = division.getId() + "-" + timeSlotKey;

                    // teacher clash
                    if (teacherTimeSlots.getOrDefault(assignedTeacher.getId().toString(), Collections.emptySet())
                            .contains(timeSlotKey)) continue;

                    // division already occupied
                    if (divisionTimeSlots.containsKey(divisionKey)) continue;

                    // skip breaks
                    if (isBreakTime(slot, allBreaks)) continue;

                    // continuous-hours rule
                    if (wouldViolateTeacherContinuousHours(assignedTeacher, day, teacherDayHours, slotDuration))
                        continue;

                    // find free classroom
                    for (Classroom classroom : suitableClassrooms) {
                        if (!classroomTimeSlots.getOrDefault(classroom.getId().toString(), Collections.emptySet())
                                .contains(timeSlotKey)) {
                            selectedSlot = slot;
                            selectedClassroom = classroom;
                            break;
                        }
                    }
                    if (selectedSlot != null) break;
                }

                if (selectedSlot != null && selectedClassroom != null) {
                    Timetable entry = new Timetable(
                            division,
                            subject,
                            assignedTeacher,
                            selectedClassroom,
                            selectedSlot,
                            false,
                            subject.getCategory() != SubjectCategory.REGULAR,
                            subject.getCategory() == SubjectCategory.REGULAR ? null : "E1",
                            null,
                            null
                    );
                    timetableEntries.add(entry);

                    String timeSlotKey = day + "-" + selectedSlot.getStartTime() + "-" + selectedSlot.getEndTime();
                    teacherTimeSlots.computeIfAbsent(assignedTeacher.getId().toString(), k -> new HashSet<>()).add(timeSlotKey);
                    classroomTimeSlots.computeIfAbsent(selectedClassroom.getId().toString(), k -> new HashSet<>()).add(timeSlotKey);
                    divisionTimeSlots.put(division.getId() + "-" + timeSlotKey, subject.getName());

                    String teacherDayKey = assignedTeacher.getId() + "-" + day;
                    teacherDayHours.computeIfAbsent(teacherDayKey, k -> new HashMap<>()).merge("hours", slotDuration, Integer::sum);

                    hoursAllocated += slotDuration;
                } else {
                    break; // no slot for this subject/division today
                }
            }
        }
    }

    private Map<Division, Teacher> distributeTeachersAcrossDivisions(List<Division> divisions, List<Teacher> availableTeachers) {
        Map<Division, Teacher> map = new HashMap<>();
        if (availableTeachers.isEmpty()) return map;
        for (int i = 0; i < divisions.size(); i++) {
            map.put(divisions.get(i), availableTeachers.get(i % availableTeachers.size()));
        }
        return map;
    }

    private boolean isBreakTime(TimeSlot slot, List<Break> allBreaks) {
        return allBreaks.stream().anyMatch(b ->
                slot.getStartTime().equals(b.getStartTime()) ||
                        slot.getEndTime().equals(b.getEndTime()) ||
                        (slot.getStartTime().compareTo(b.getStartTime()) >= 0 &&
                                slot.getEndTime().compareTo(b.getEndTime()) <= 0)
        );
    }

    private boolean wouldViolateTeacherContinuousHours(Teacher teacher, String day,
                                                       Map<String, Map<String, Integer>> teacherDayHours,
                                                       int slotDuration) {
        String key = teacher.getId() + "-" + day;
        int current = teacherDayHours.getOrDefault(key, Collections.emptyMap()).getOrDefault("hours", 0);
        return (current + slotDuration) > 4;
    }
}

// src/main/java/com/rspc/timetable/services/TimetableGeneratorService.java
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
            TeacherSubjectAllocationService teacherSubjectAllocationService
    ) {
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

    // Full generation across all years/semesters
    @Transactional
    public String generateCompleteTimetable() {
        timetableRepository.deleteAll();

        List<Division> divisions   = divisionService.getAllDivisions();
        List<Subject> subjects     = subjectService.getAllSubjects();
        List<Classroom> classrooms = classroomService.getAllClassrooms();
        List<TimeSlot> timeSlots   = timeSlotService.getAllTimeSlots();

        validatePrereqs(divisions, subjects, classrooms, timeSlots);

        // Sort by start time only; weekday is chosen per Timetable row
        timeSlots.sort(Comparator.comparing(TimeSlot::getStartTime, Comparator.nullsLast(String::compareTo)));

        List<Timetable> generated = new ArrayList<>();

        for (Division div : divisions) {
            Long divYearId = div.getYear() != null ? div.getYear().getId() : null;
            Long divSemId  = div.getSemester() != null ? div.getSemester().getId() : null;
            if (divYearId == null || divSemId == null) continue;

            List<Subject> divSubjects = subjects.stream()
                .filter(s -> s.getYear()!=null && s.getSemester()!=null)
                .filter(s -> Objects.equals(s.getYear().getId(), divYearId)
                          && Objects.equals(s.getSemester().getId(), divSemId))
                .toList();

            generated.addAll(generateForDivision(div, divSubjects, classrooms, timeSlots));
        }

        timetableRepository.saveAll(generated);
        return "Timetable generated successfully for " + generated.size() + " entries";
    }

    // Generate for a single year (e.g., Year 1)
    @Transactional
    public String generateForYear(Long yearId) {
        List<Division> allDivisions   = divisionService.getAllDivisions();
        List<Subject>  allSubjects    = subjectService.getAllSubjects();
        List<Classroom> classrooms    = classroomService.getAllClassrooms();
        List<TimeSlot>  timeSlots     = timeSlotService.getAllTimeSlots();

        List<Division> divisions = allDivisions.stream()
            .filter(d -> d.getYear()!=null && d.getSemester()!=null
                      && Objects.equals(d.getYear().getId(), yearId))
            .toList();

        if (divisions.isEmpty())  throw new IllegalStateException("No divisions for yearId=" + yearId);
        if (classrooms.isEmpty()) throw new IllegalStateException("No classrooms found");
        if (timeSlots.isEmpty())  throw new IllegalStateException("No time slots found");

        List<Subject> subjectsThisYear = allSubjects.stream()
            .filter(s -> s.getYear()!=null && s.getSemester()!=null
                      && Objects.equals(s.getYear().getId(), yearId))
            .toList();

        if (subjectsThisYear.isEmpty()) throw new IllegalStateException("No subjects for yearId=" + yearId);

        timeSlots.sort(Comparator.comparing(TimeSlot::getStartTime, Comparator.nullsLast(String::compareTo)));

        List<Timetable> generated = new ArrayList<>();

        for (Division div : divisions) {
            Long semId = div.getSemester().getId();
            List<Subject> divSubjects = subjectsThisYear.stream()
                .filter(s -> Objects.equals(s.getSemester().getId(), semId))
                .toList();

            generated.addAll(generateForDivision(div, divSubjects, classrooms, timeSlots));
        }

        timetableRepository.saveAll(generated);
        return "Timetable generated for yearId=" + yearId + " with " + generated.size() + " entries";
    }

    // Generate for a specific year + semester
    @Transactional
    public String generateForTerm(Long yearId, Long semesterId) {
        List<Division> allDivisions   = divisionService.getAllDivisions();
        List<Subject>  allSubjects    = subjectService.getAllSubjects();
        List<Classroom> classrooms    = classroomService.getAllClassrooms();
        List<TimeSlot>  timeSlots     = timeSlotService.getAllTimeSlots();

        List<Division> divisions = allDivisions.stream()
            .filter(d -> d.getYear()!=null && d.getSemester()!=null
                      && Objects.equals(d.getYear().getId(), yearId)
                      && Objects.equals(d.getSemester().getId(), semesterId))
            .toList();

        List<Subject> subjects = allSubjects.stream()
            .filter(s -> s.getYear()!=null && s.getSemester()!=null
                      && Objects.equals(s.getYear().getId(), yearId)
                      && Objects.equals(s.getSemester().getId(), semesterId))
            .toList();

        if (divisions.isEmpty())  throw new IllegalStateException("No divisions for yearId=" + yearId + ", semesterId=" + semesterId);
        if (subjects.isEmpty())   throw new IllegalStateException("No subjects for yearId=" + yearId + ", semesterId=" + semesterId);
        if (classrooms.isEmpty()) throw new IllegalStateException("No classrooms found");
        if (timeSlots.isEmpty())  throw new IllegalStateException("No time slots found");

        timeSlots.sort(Comparator.comparing(TimeSlot::getStartTime, Comparator.nullsLast(String::compareTo)));

        List<Timetable> generated = new ArrayList<>();
        for (Division div : divisions) {
            generated.addAll(generateForDivision(div, subjects, classrooms, timeSlots));
        }

        timetableRepository.saveAll(generated);
        return "Timetable generated for yearId=" + yearId + ", semesterId=" + semesterId
                + " with " + generated.size() + " entries";
    }

    // Validate prerequisites up-front
    private void validatePrereqs(List<Division> divisions, List<Subject> subjects,
                                 List<Classroom> classrooms, List<TimeSlot> timeSlots) {
        if (divisions == null || divisions.isEmpty())  throw new IllegalStateException("No divisions found");
        if (subjects == null  || subjects.isEmpty())   throw new IllegalStateException("No subjects found");
        if (classrooms == null|| classrooms.isEmpty()) throw new IllegalStateException("No classrooms found");
        if (timeSlots == null || timeSlots.isEmpty())  throw new IllegalStateException("No time slots found");
        if (subjects.stream().anyMatch(s -> s.getYear()==null || s.getSemester()==null))
            throw new IllegalStateException("Some subjects have null year or semester");
    }

    // Balanced day-aware scheduler using dayless TimeSlot templates
    private List<Timetable> generateForDivision(
            Division div,
            List<Subject> divSubjects,
            List<Classroom> classrooms,
            List<TimeSlot> timeSlots
    ) {
        List<Timetable> out = new ArrayList<>();
        if (divSubjects == null || divSubjects.isEmpty()) return out;

        // Define working week; adjust as needed
        List<String> days = List.of("MON","TUE","WED","THU","FRI");
        Map<String,Integer> dayCounts = new HashMap<>();
        Map<String,Integer> dayCursor = new HashMap<>();
        days.forEach(d -> { dayCounts.put(d, 0); dayCursor.put(d, -1); });

        // Time-of-day templates shared across days
        List<TimeSlot> slotsByTime = new ArrayList<>(timeSlots);
        slotsByTime.sort(Comparator.comparing(TimeSlot::getStartTime, Comparator.nullsLast(String::compareTo)));

        for (Subject subj : divSubjects) {
            boolean isLab = SubjectType.LAB.equals(subj.getType());
            int sessions  = isLab ? 1 : Math.max(1, subj.getCredits() != null ? subj.getCredits() : 1);

            Classroom room = classrooms.stream()
                .filter(c -> isLab ? c.getType() == ClassroomType.LAB : c.getType() != ClassroomType.LAB)
                .findFirst()
                .orElseGet(() -> classrooms.get(0));

            // Optional teacher pick; returns null if none found
            Long yearId = div.getYear() != null ? div.getYear().getId() : null;
            Teacher picked = teacherSubjectAllocationService.pickAnyTeacherFor(subj.getId(), yearId);

            for (int k = 0; k < sessions; k++) {
                // choose least-loaded day
                String bestDay = days.stream()
                        .min(Comparator.comparingInt(dayCounts::get))
                        .orElse("MON");

                // next time slot for that day (shared template)
                int next = (dayCursor.get(bestDay) + 1) % slotsByTime.size();
                dayCursor.put(bestDay, next);
                TimeSlot ts = slotsByTime.get(next);

                boolean elective = subj.getCategory() == SubjectCategory.OPEN_ELECTIVE
                        || subj.getCategory() == SubjectCategory.PROGRAM_ELECTIVE;
                String group = subj.getCategory() == SubjectCategory.OPEN_ELECTIVE ? "1"
                        : (subj.getCategory() == SubjectCategory.PROGRAM_ELECTIVE ? "2" : null);

                // Timetable.day must exist as a column on the table/entity
                // inside TimetableGeneratorService.generateForDivision(...)
Timetable tt = new Timetable(
    div, subj, picked, room, ts, bestDay,
    false, elective, group, null, null
);

                out.add(tt);
                dayCounts.merge(bestDay, 1, Integer::sum);
            }
        }
        return out;
    }
}

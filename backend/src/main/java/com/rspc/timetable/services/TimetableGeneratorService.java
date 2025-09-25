// src/main/java/com/rspc/timetable/services/TimetableGeneratorService.java
package com.rspc.timetable.services;

import com.rspc.timetable.entities.*;
import com.rspc.timetable.repositories.TimetableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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

    @Transactional
    public String generateCompleteTimetable() {
        // 1) Start fresh
        timetableRepository.deleteAll();

        // 2) Load all prerequisite data
        List<Division> divisions   = divisionService.getAllDivisions();
        List<Subject> subjects     = subjectService.getAllSubjects();
        List<Classroom> classrooms = classroomService.getAllClassrooms();
        List<TimeSlot> timeSlots   = timeSlotService.getAllTimeSlots();
        List<Break> breaks         = breakService.getAll(); // optional use in conflict checks

        // 3) Preconditions to produce clear, actionable errors
        if (divisions == null || divisions.isEmpty()) {
            throw new IllegalStateException("No divisions found");
        }
        if (subjects == null || subjects.isEmpty()) {
            throw new IllegalStateException("No subjects found");
        }
        if (classrooms == null || classrooms.isEmpty()) {
            throw new IllegalStateException("No classrooms found");
        }
        if (timeSlots == null || timeSlots.isEmpty()) {
            throw new IllegalStateException("No time slots found");
        }
        if (subjects.stream().anyMatch(s -> s.getYear() == null || s.getSemester() == null)) {
            throw new IllegalStateException("Some subjects have null year or semester");
        }

        // 4) Sort time slots deterministically (by day, then start time) to keep output stable
        timeSlots.sort(Comparator
                .comparing(TimeSlot::getDayOfWeek, Comparator.nullsLast(String::compareTo))
                .thenComparing(TimeSlot::getStartTime, Comparator.nullsLast(String::compareTo)));

        // 5) Minimal illustrative scheduling:
        //    - For each division, schedule its subjects that match division's year/semester
        //    - Assign a classroom (LAB for lab subjects, else NORMAL when available)
        //    - Allocate one slot per required weekly session (lecPerWeek approx by credits for THEORY, 1 for LAB)
        List<Timetable> generated = new ArrayList<>();

        for (Division div : divisions) {
            Long divYearId = div.getYear() != null ? div.getYear().getId() : null;
            Long divSemId  = div.getSemester() != null ? div.getSemester().getId() : null;

            // Skip divisions without proper linkage
            if (divYearId == null || divSemId == null) continue;

            // Subjects matching this division's term
            List<Subject> divSubjects = subjects.stream()
                    .filter(s ->
                            Objects.equals(s.getYear().getId(), divYearId) &&
                            Objects.equals(s.getSemester().getId(), divSemId))
                    .toList();

            // Round-robin through time slots for this division
            int slotIdx = 0;

            for (Subject subj : divSubjects) {
                boolean isLab = SubjectType.LAB.equals(subj.getType());
                int sessions = isLab ? 1 : Math.max(1, subj.getCredits() != null ? subj.getCredits() : 1);

                // Pick a classroom by type (LAB uses LAB room; else prefer NORMAL, fallback to any)
                Classroom room = classrooms.stream()
                        .filter(c -> isLab ? c.getType() == ClassroomType.LAB : c.getType() != ClassroomType.LAB)
                        .findFirst()
                        .orElseGet(() -> classrooms.get(0));

                for (int k = 0; k < sessions; k++) {
                    TimeSlot ts = timeSlots.get(slotIdx % timeSlots.size());
                    slotIdx++;

                    Timetable tt = new Timetable(
                            div,
                            subj,
                            null,              // Teacher mapping can be filled from TeacherSubjectAllocationService if desired
                            room,
                            ts,
                            false,             // isOverride
                            subj.getCategory() == SubjectCategory.OPEN_ELECTIVE ||
                            subj.getCategory() == SubjectCategory.PROGRAM_ELECTIVE,
                            subj.getCategory() == SubjectCategory.OPEN_ELECTIVE ? "1" :
                            (subj.getCategory() == SubjectCategory.PROGRAM_ELECTIVE ? "2" : null),
                            null,              // overrideStartDate
                            null               // overrideEndDate
                    );

                    generated.add(tt);
                }
            }
        }

        // 6) Persist
        timetableRepository.saveAll(generated);

        // 7) Done
        return "Timetable generated successfully for " + generated.size() + " entries";
    }
}

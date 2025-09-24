package com.rspc.timetable.services;

import com.rspc.timetable.dto.ChangeType;
import com.rspc.timetable.dto.TimetableChangeRequest;
import com.rspc.timetable.entities.*;
import com.rspc.timetable.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TimetableChangeService {

    private final TimetableRepository timetableRepo;
    private final TeacherRepository teacherRepo;
    private final ClassroomRepository classroomRepo;
    private final SubjectRepository subjectRepo;
    private final TimeSlotRepository timeSlotRepo;
    private final DivisionRepository divisionRepo;

    public TimetableChangeService(
            TimetableRepository timetableRepo,
            TeacherRepository teacherRepo,
            ClassroomRepository classroomRepo,
            SubjectRepository subjectRepo,
            TimeSlotRepository timeSlotRepo,
            DivisionRepository divisionRepo) {
        this.timetableRepo = timetableRepo;
        this.teacherRepo = teacherRepo;
        this.classroomRepo = classroomRepo;
        this.subjectRepo = subjectRepo;
        this.timeSlotRepo = timeSlotRepo;
        this.divisionRepo = divisionRepo;
    }

    @Transactional
    public List<Timetable> applyChange(TimetableChangeRequest req) {
        return (req.getType() == ChangeType.TEMPORARY) ? applyTemporary(req) : applyPermanent(req);
    }

    // Temporary = write dated override rows, keep base unchanged
    @Transactional
    private List<Timetable> applyTemporary(TimetableChangeRequest req) {
        LocalDate from = Objects.requireNonNull(req.getDateFrom(), "dateFrom required for TEMPORARY");
        LocalDate to = (req.getDateTo() != null) ? req.getDateTo() : from;

        // Base rows: override* must be null
        List<Timetable> base = timetableRepo.findByDivision_IdAndOverrideStartDateIsNull(req.getDivisionId());

        if (req.getSubjectId() != null) {
            base = base.stream()
                    .filter(t -> t.getSubject() != null && Objects.equals(t.getSubject().getId(), req.getSubjectId()))
                    .collect(Collectors.toList());
        }
        if (req.getFromTeacherId() != null) {
            base = base.stream()
                    .filter(t -> t.getTeacher() != null && Objects.equals(t.getTeacher().getId(), req.getFromTeacherId()))
                    .collect(Collectors.toList());
        }
        if (req.getTimeSlotIds() != null && !req.getTimeSlotIds().isEmpty()) {
            Set<Long> slotSet = new HashSet<>(req.getTimeSlotIds());
            base = base.stream()
                    .filter(t -> t.getTimeSlot() != null && slotSet.contains(t.getTimeSlot().getId()))
                    .collect(Collectors.toList());
        }

        Teacher toTeacher = (req.getToTeacherId() != null)
                ? teacherRepo.findById(req.getToTeacherId()).orElse(null) : null;
        Classroom toClassroom = (req.getClassroomId() != null)
                ? classroomRepo.findById(req.getClassroomId()).orElse(null) : null;

        List<Timetable> overrides = new ArrayList<>();
        for (Timetable b : base) {
            Timetable o = new Timetable(
                    b.getDivision(),
                    b.getSubject(),
                    (toTeacher != null ? toTeacher : b.getTeacher()),
                    (toClassroom != null ? toClassroom : b.getClassroom()),
                    b.getTimeSlot(),
                    true,                 // isOverride
                    b.isElective(),       // copy elective flag
                    b.getElectiveGroup(), // copy elective group (String)
                    from,
                    to
            );
            overrides.add(o);
        }
        return timetableRepo.saveAll(overrides);
    }

    // Permanent = edit base rows (override* must stay null)
    @Transactional
    private List<Timetable> applyPermanent(TimetableChangeRequest req) {
        List<Timetable> base = timetableRepo.findByDivision_IdAndOverrideStartDateIsNull(req.getDivisionId());

        if (req.getSubjectId() != null) {
            base = base.stream()
                    .filter(t -> t.getSubject() != null && Objects.equals(t.getSubject().getId(), req.getSubjectId()))
                    .collect(Collectors.toList());
        }
        if (req.getFromTeacherId() != null) {
            base = base.stream()
                    .filter(t -> t.getTeacher() != null && Objects.equals(t.getTeacher().getId(), req.getFromTeacherId()))
                    .collect(Collectors.toList());
        }
        if (req.getTimeSlotIds() != null && !req.getTimeSlotIds().isEmpty()) {
            Set<Long> slotSet = new HashSet<>(req.getTimeSlotIds());
            base = base.stream()
                    .filter(t -> t.getTimeSlot() != null && slotSet.contains(t.getTimeSlot().getId()))
                    .collect(Collectors.toList());
        }

        Teacher toTeacher = (req.getToTeacherId() != null)
                ? teacherRepo.findById(req.getToTeacherId()).orElse(null) : null;
        Classroom toClassroom = (req.getClassroomId() != null)
                ? classroomRepo.findById(req.getClassroomId()).orElse(null) : null;

        for (Timetable t : base) {
            if (toTeacher != null) t.setTeacher(toTeacher);
            if (toClassroom != null) t.setClassroom(toClassroom);
            t.setOverrideStartDate(null);
            t.setOverrideEndDate(null);
        }
        return timetableRepo.saveAll(base);
    }
}

package com.rspc.timetable.controllers;

import com.rspc.timetable.entities.Subject;
import com.rspc.timetable.entities.TeacherSubjectAllocation;
import com.rspc.timetable.entities.TeacherSubjectAllocation.Role;
import com.rspc.timetable.repositories.SubjectRepository;
import com.rspc.timetable.services.TeacherSubjectAllocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/teacher-allocations")
public class TeacherAllocationController {

    private final SubjectRepository subjectRepository;
    private final TeacherSubjectAllocationService service;

    public TeacherAllocationController(SubjectRepository subjectRepository,
                                       TeacherSubjectAllocationService service) {
        this.subjectRepository = subjectRepository;
        this.service = service;
    }

    // Minimal payload: subject_id + teacher_ids (+ optional role override)
    public static record BulkByIdItem(Long subject_id, List<Long> teacher_ids, String role) {}

    @PostMapping("/bulk-by-id")
    public ResponseEntity<?> bulkById(@RequestBody List<BulkByIdItem> items) {
        try {
            List<TeacherSubjectAllocation> rows = new ArrayList<>();

            for (BulkByIdItem it : items) {
                Objects.requireNonNull(it.subject_id(), "subject_id is required");
                if (it.teacher_ids() == null || it.teacher_ids().isEmpty()) {
                    throw new IllegalArgumentException("teacher_ids is required and cannot be empty");
                }

                Subject s = subjectRepository.findById(it.subject_id())
                        .orElseThrow(() -> new RuntimeException("Subject not found by id: " + it.subject_id()));

                Long yearId = s.getYear().getId();
                Long semesterId = s.getSemester().getId();

                Role derivedRole = "LAB".equalsIgnoreCase(s.getType().name()) ? Role.LAB : Role.THEORY;
                Role finalRole = (it.role() != null && !it.role().isBlank())
                        ? Role.valueOf(it.role().trim().toUpperCase())
                        : derivedRole;

                for (Long teacherId : it.teacher_ids()) {
                    TeacherSubjectAllocation t = new TeacherSubjectAllocation();
                    t.setSubjectId(s.getId());
                    t.setTeacherId(teacherId);
                    t.setYearId(yearId);
                    t.setSemesterId(semesterId);
                    t.setRole(finalRole);
                    rows.add(t);
                }
            }

            return ResponseEntity.ok(service.saveAllEntities(rows));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid input: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Bulk insert failed: " + e.getMessage());
        }
    }

    // Other existing endpoints remain unchanged
}

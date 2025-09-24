package com.rspc.timetable.controllers;

import com.rspc.timetable.entities.Subject;
import com.rspc.timetable.entities.TeacherSubjectAllocation;
import com.rspc.timetable.entities.TeacherSubjectAllocation.Role;
import com.rspc.timetable.repositories.SubjectRepository;
import com.rspc.timetable.services.TeacherSubjectAllocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.Normalizer;
import java.util.*;

@RestController
@RequestMapping("/api/teacher-allocations")
public class TeacherAllocationController {

  public static record BulkItem(String subject_name, Long year_id, String role, List<Long> teacher_ids) {}

  private final SubjectRepository subjectRepository;
  private final TeacherSubjectAllocationService service;

  public TeacherAllocationController(SubjectRepository subjectRepository,
                                     TeacherSubjectAllocationService service) {
    this.subjectRepository = subjectRepository;
    this.service = service;
  }

  @PostMapping("/bulk")
  public ResponseEntity<?> bulk(@RequestBody List<BulkItem> items) {
    try {
      List<Subject> allSubjects = subjectRepository.findAll();
      List<TeacherSubjectAllocation> rows = new ArrayList<>();

      for (BulkItem it : items) {
        Subject s = resolveSubject(allSubjects, it.subject_name(), it.year_id());
        Role role = Role.valueOf(Optional.ofNullable(it.role()).orElse("THEORY"));
        for (Long teacherId : it.teacher_ids()) {
          TeacherSubjectAllocation t = new TeacherSubjectAllocation();
          t.setSubjectId(s.getId());
          t.setTeacherId(teacherId);
          t.setYearId(it.year_id());
          t.setRole(role);
          rows.add(t);
        }
      }
      return ResponseEntity.ok(service.saveAllEntities(rows));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body("Invalid role; use THEORY or LAB: " + e.getMessage());
    } catch (RuntimeException e) {
      return ResponseEntity.badRequest().body("Bulk insert failed: " + e.getMessage());
    }
  }

  private Subject resolveSubject(List<Subject> all, String name, Long yearId) {
    String norm = normalize(name);
    return all.stream()
      .filter(s -> s.getYear() != null && Objects.equals(s.getYear().getId(), yearId))
      .filter(s -> normalize(s.getName()).equalsIgnoreCase(norm)
                || normalize(s.getName()).contains(norm))
      .findFirst()
      .orElseThrow(() -> new RuntimeException("Subject not found: '" + name + "' for year_id=" + yearId));
  }

  private static String normalize(String s) {
    if (s == null) return "";
    String n = Normalizer.normalize(s, Normalizer.Form.NFKC);
    n = n.replace('–','-').replace('—','-');
    n = n.replaceAll("[\\p{Punct}&&[^-]]", " ");
    n = n.replaceAll("\\s+"," ").trim().toLowerCase(Locale.ROOT);
    return n;
  }
}

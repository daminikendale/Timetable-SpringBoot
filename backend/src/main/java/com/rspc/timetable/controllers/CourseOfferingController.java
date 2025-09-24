package com.rspc.timetable.controllers;

import com.rspc.timetable.entities.CourseOffering;
import com.rspc.timetable.repositories.CourseOfferingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/offerings")
public class CourseOfferingController {

    public static record BulkOfferingRequest(
        Long subjectId, 
        Long yearId, 
        Long semesterId, 
        Long divisionId, 
        Integer lecPerWeek, 
        Integer tutPerWeek, 
        Integer labPerWeek, 
        Boolean isElective, 
        Long electiveGroupId
    ) {}

    private final CourseOfferingRepository repository;

    public CourseOfferingController(CourseOfferingRepository repository) {
        this.repository = repository;
    }

   @PostMapping("/bulk")
@Transactional
public ResponseEntity<?> bulkInsert(@RequestBody List<BulkOfferingRequest> reqs) {
  try {
    List<CourseOffering> toSave = new ArrayList<>();
    for (var r : reqs) {
      // FK sanity (replace with actual repos/services)
      if (r.subjectId() == null || r.yearId() == null || r.semesterId() == null) {
        return ResponseEntity.badRequest().body("subjectId/yearId/semesterId required");
      }
      if (repository.existsBySubjectIdAndYearIdAndSemesterId(r.subjectId(), r.yearId(), r.semesterId())) {
        continue; // skip duplicate
      }
      CourseOffering o = new CourseOffering();
      o.setSubjectId(r.subjectId());
      o.setYearId(r.yearId());
      o.setSemesterId(r.semesterId());
      o.setLecPerWeek(r.lecPerWeek() != null ? r.lecPerWeek() : 0);
      o.setTutPerWeek(r.tutPerWeek() != null ? r.tutPerWeek() : 0);
      o.setLabPerWeek(r.labPerWeek() != null ? r.labPerWeek() : 0);
      o.setIsElective(r.isElective() != null ? r.isElective() : false);
      o.setElectiveGroupId(r.electiveGroupId());
      toSave.add(o);
    }
    return ResponseEntity.ok(repository.saveAll(toSave));
  } catch (Exception e) {
    return ResponseEntity.status(500).body("Bulk insert failed: " + e.getMessage());
  }
}

}

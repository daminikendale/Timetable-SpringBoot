package com.rspc.timetable.controllers;

import com.rspc.timetable.entities.Semester;
import com.rspc.timetable.services.SemesterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.rspc.timetable.dto.SemesterDTO;
import java.util.List;

@RestController
@RequestMapping("/api/semesters")
public class SemesterController {

    private final SemesterService semesterService;

    public SemesterController(SemesterService semesterService) {
        this.semesterService = semesterService;
    }

    // Get all semesters
    @GetMapping
    public List<Semester> getAllSemesters() {
        return semesterService.getAllSemesters();
    }

    // Get a semester by ID
    @GetMapping("/{id}")
    public ResponseEntity<Semester> getSemesterById(@PathVariable Long id) {
        return semesterService.getSemesterById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create a single semester
    @PostMapping
    public Semester createSemester(@RequestBody Semester semester) {
        return semesterService.saveSemester(semester);
    }

    // Bulk create semesters
    @PostMapping("/bulk")
    public List<Semester> createSemesters(@RequestBody List<Semester> semesters) {
        return semesterService.saveSemesters(semesters);
    }

    // Delete a semester
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSemester(@PathVariable Long id) {
        semesterService.deleteSemester(id);
        return ResponseEntity.noContent().build();
    }

    
@GetMapping("/dto")
public ResponseEntity<List<SemesterDTO>> getAllSemesterDTOs() {
    return ResponseEntity.ok(semesterService.getAllSemesterDTOs());
}
}

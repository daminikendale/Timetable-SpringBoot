package com.rspc.timetable.controllers;

import com.rspc.timetable.dto.SemesterDTO;
import com.rspc.timetable.entities.Semester;
import com.rspc.timetable.services.SemesterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/semesters")
public class SemesterController {

    private final SemesterService semesterService;

    public SemesterController(SemesterService semesterService) {
        this.semesterService = semesterService;
    }

    // ----- Entity endpoints -----

    @GetMapping
    public List<Semester> getAllSemesters() {
        return semesterService.getAllSemesters();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Semester> getSemesterById(@PathVariable Long id) {
        return semesterService.getSemesterById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Semester createSemester(@RequestBody Semester semester) {
        return semesterService.saveSemester(semester);
    }

    @PostMapping("/bulk")
    public List<Semester> createSemesters(@RequestBody List<Semester> semesters) {
        return semesterService.saveSemesters(semesters);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSemester(@PathVariable Long id) {
        semesterService.deleteSemester(id);
        return ResponseEntity.noContent().build();
    }

    // ----- DTO endpoints (separate to avoid type mismatches) -----

    @GetMapping("/dto")
    public ResponseEntity<List<SemesterDTO>> getAllSemesterDTOs() {
        return ResponseEntity.ok(semesterService.getAllSemesterDTOs());
    }

    @GetMapping("/dto/{id}")
    public ResponseEntity<SemesterDTO> getSemesterDTOById(@PathVariable Long id) {
        return semesterService.getSemesterDTOById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

package com.rspc.timetable.controllers;

import com.rspc.timetable.dto.SubjectDTO;
import com.rspc.timetable.services.SubjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects") // Pluralized for RESTful convention
@CrossOrigin(origins = "http://localhost:5173") // Enables CORS for your frontend
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    // GET /api/subjects - Get all subjects
    @GetMapping
    public ResponseEntity<List<SubjectDTO>> getAllSubjects() {
        return ResponseEntity.ok(subjectService.getAllSubjects());
    }

    // GET /api/subjects/{id} - Get a single subject by ID
    @GetMapping("/{id}")
    public ResponseEntity<SubjectDTO> getSubjectById(@PathVariable Long id) {
        return ResponseEntity.ok(subjectService.getSubjectById(id));
    }

    // POST /api/subjects - Create a new subject
    @PostMapping
    public ResponseEntity<SubjectDTO> createSubject(@RequestBody SubjectDTO subjectDTO) {
        SubjectDTO createdSubject = subjectService.createSubject(subjectDTO);
        return new ResponseEntity<>(createdSubject, HttpStatus.CREATED);
    }

    // PUT /api/subjects/{id} - Update an existing subject
    @PutMapping("/{id}")
    public ResponseEntity<SubjectDTO> updateSubject(@PathVariable Long id, @RequestBody SubjectDTO subjectDTO) {
        SubjectDTO updatedSubject = subjectService.updateSubject(id, subjectDTO);
        return ResponseEntity.ok(updatedSubject);
    }

    // DELETE /api/subjects/{id} - Delete a subject
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.noContent().build();
    }

    // POST /api/subjects/bulk - Bulk create subjects
    @PostMapping("/bulk")
    public ResponseEntity<List<SubjectDTO>> createSubjectsBulk(@RequestBody List<SubjectDTO> subjectDTOs) {
        List<SubjectDTO> createdSubjects = subjectService.createSubjectsBulk(subjectDTOs);
        return new ResponseEntity<>(createdSubjects, HttpStatus.CREATED);
    }
}

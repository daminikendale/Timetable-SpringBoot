package com.rspc.timetable.controllers;

import com.rspc.timetable.dto.SubjectDTO;
import com.rspc.timetable.entities.Subject;
import com.rspc.timetable.services.SubjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.rspc.timetable.dto.SubjectDTO;
import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Subject>> createSubjects(@RequestBody List<SubjectDTO> subjectDTOs) {
        List<Subject> savedSubjects = subjectService.saveAllSubjects(subjectDTOs);
        return ResponseEntity.ok(savedSubjects);
    }

    @GetMapping
    public ResponseEntity<List<Subject>> getAllSubjects() {
        return ResponseEntity.ok(subjectService.getAllSubjects());
    }

    @GetMapping("/dto")
public ResponseEntity<List<SubjectDTO>> getAllSubjectDTOs() {
    return ResponseEntity.ok(subjectService.getAllSubjectDTOs());
}
}

package com.rspc.timetable.controllers;

import com.rspc.timetable.dto.SubjectDTO;
import com.rspc.timetable.entities.Subject;
import com.rspc.timetable.services.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping("/ping")
    public String ping() { return "ok"; }

    @PostMapping("/bulk")
    public ResponseEntity<List<Subject>> bulkCreateOrUpdate(
            @RequestBody @Valid List<SubjectDTO> payload) {
        return ResponseEntity.ok(subjectService.createOrUpdateBulk(payload));
    }
}

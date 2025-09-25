// src/main/java/com/rspc/timetable/controllers/TimetableGeneratorController.java
package com.rspc.timetable.controllers;

import com.rspc.timetable.services.TimetableGeneratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(
    origins = {"http://localhost:3000", "http://127.0.0.1:3000"},
    allowCredentials = "true"
)
@RestController
@RequestMapping("/api/timetable-generator")

public class TimetableGeneratorController {

    private final TimetableGeneratorService timetableGeneratorService;

    public TimetableGeneratorController(TimetableGeneratorService timetableGeneratorService) {
        this.timetableGeneratorService = timetableGeneratorService;
    }

    // One route: full, year-only, or term-only based on params
    @PostMapping("/generate")
    public ResponseEntity<?> generateTimetable(
            @RequestParam(required = false) Long yearId,
            @RequestParam(required = false) Long semesterId
    ) {
        if (yearId != null && semesterId != null) {
            return ResponseEntity.ok(timetableGeneratorService.generateForTerm(yearId, semesterId));
        } else if (yearId != null) {
            return ResponseEntity.ok(timetableGeneratorService.generateForYear(yearId));
        } else {
            return ResponseEntity.ok(timetableGeneratorService.generateCompleteTimetable());
        }
    }

    // Explicit year-only route
    @PostMapping("/generate-year")
    public ResponseEntity<?> generateForYear(@RequestParam Long yearId) {
        return ResponseEntity.ok(timetableGeneratorService.generateForYear(yearId));
    }

    // Explicit term-only route
    @PostMapping("/generate-term")
    public ResponseEntity<?> generateForTerm(
        @RequestParam Long yearId,
        @RequestParam Long semesterId
    ) {
        return ResponseEntity.ok(timetableGeneratorService.generateForTerm(yearId, semesterId));
    }
}

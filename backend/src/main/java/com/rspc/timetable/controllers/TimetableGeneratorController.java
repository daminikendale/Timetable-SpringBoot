package com.rspc.timetable.controllers;

import com.rspc.timetable.services.TimetableGeneratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/timetable-generator")
@CrossOrigin(origins = "*")
public class TimetableGeneratorController {

    private final TimetableGeneratorService timetableGeneratorService;

    public TimetableGeneratorController(TimetableGeneratorService timetableGeneratorService) {
        this.timetableGeneratorService = timetableGeneratorService;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateTimetable() {
        try {
            String result = timetableGeneratorService.generateCompleteTimetable();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Failed to generate timetable: " + e.getMessage());
        }
    }
}
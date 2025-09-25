// src/main/java/com/rspc/timetable/controllers/TimetableGeneratorController.java
package com.rspc.timetable.controllers;

import com.rspc.timetable.services.TimetableGeneratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/timetable-generator")
public class TimetableGeneratorController {

    private final TimetableGeneratorService timetableGeneratorService;

    public TimetableGeneratorController(TimetableGeneratorService timetableGeneratorService) {
        this.timetableGeneratorService = timetableGeneratorService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateTimetable(
            @RequestParam(required = false) Long yearId,
            @RequestParam(required = false) Long semesterId
    ) {
        try {
            String result;
            if (yearId != null && semesterId != null) {
                result = timetableGeneratorService.generateForTerm(yearId, semesterId);
            } else if (yearId != null) {
                result = timetableGeneratorService.generateForYear(yearId);
            } else {
                result = timetableGeneratorService.generateCompleteTimetable();
            }
            return ResponseEntity.ok(result);
        } catch (Throwable e) {
            String body = String.format(
                "{\"error\":\"%s\",\"message\":\"%s\"}",
                e.getClass().getSimpleName(),
                String.valueOf(e.getMessage())
            );
            return ResponseEntity.internalServerError().body(body);
        }
    }

    @PostMapping("/generate-year")
    public ResponseEntity<?> generateForYear(@RequestParam Long yearId) {
        try {
            return ResponseEntity.ok(timetableGeneratorService.generateForYear(yearId));
        } catch (Throwable e) {
            String body = String.format(
                "{\"error\":\"%s\",\"message\":\"%s\"}",
                e.getClass().getSimpleName(),
                String.valueOf(e.getMessage())
            );
            return ResponseEntity.internalServerError().body(body);
        }
    }

    @PostMapping("/generate-term")
    public ResponseEntity<?> generateForTerm(@RequestParam Long yearId, @RequestParam Long semesterId) {
        try {
            return ResponseEntity.ok(timetableGeneratorService.generateForTerm(yearId, semesterId));
        } catch (Throwable e) {
            String body = String.format(
                "{\"error\":\"%s\",\"message\":\"%s\"}",
                e.getClass().getSimpleName(),
                String.valueOf(e.getMessage())
            );
            return ResponseEntity.internalServerError().body(body);
        }
    }
}

// src/main/java/com/rspc/timetable/controllers/TimetableGeneratorController.java
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
    public ResponseEntity<?> generateTimetable() {
        try {
            String result = timetableGeneratorService.generateCompleteTimetable();
            return ResponseEntity.ok(result);
        } catch (Throwable e) { // temporarily broaden for visibility during debugging
            // TODO: inject logger and log stack trace: log.error("Generation failed", e);
            String body = String.format("{\"error\":\"%s\",\"message\":\"%s\"}",
                    e.getClass().getSimpleName(),
                    String.valueOf(e.getMessage()));
            return ResponseEntity.internalServerError().body(body);
        }
    }
}

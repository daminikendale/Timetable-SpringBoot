package com.rspc.timetable.controllers;

import com.rspc.timetable.services.TimetableGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/timetable-generator")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TimetableGeneratorController {

    private final TimetableGeneratorService timetableGeneratorService;

    /**
     * Triggers the timetable generation process for a specific semester type.
     * Example URLs:
     * POST /api/timetable-generator/generate/ODD
     * POST /api/timetable-generator/generate/EVEN
     */
    @PostMapping("/generate/{semesterType}")
    public ResponseEntity<String> generateTimetable(
        @PathVariable("semesterType") TimetableGeneratorService.SemesterType semesterType) {
        
        try {
            String result = timetableGeneratorService.generateTimetableFor(semesterType);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // In a real app, a @ControllerAdvice would handle this more gracefully
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}

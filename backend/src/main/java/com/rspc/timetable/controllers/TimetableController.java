package com.rspc.timetable.controllers;

import com.rspc.timetable.optaplanner.TimetableOptimizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/timetable")
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableOptimizeService optimizeService;

    @PostMapping("/generate/optimized/{semesterNumber}")
    public ResponseEntity<String> generateOptimized(@PathVariable int semesterNumber) {
        return ResponseEntity.ok(optimizeService.generateOptimized(semesterNumber));
    }
}

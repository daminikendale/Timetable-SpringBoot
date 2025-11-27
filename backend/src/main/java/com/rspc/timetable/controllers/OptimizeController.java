package com.rspc.timetable.controllers;

import com.rspc.timetable.dto.SolveResultDTO;
import com.rspc.timetable.optaplanner.TimetableOptaPlannerService;
import com.rspc.timetable.optaplanner.TimetableSolution;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/optimize")
@RequiredArgsConstructor
public class OptimizeController {

    private final TimetableOptaPlannerService service;

    @PostMapping("/solve/{semesterId}")
    public ResponseEntity<?> solveSync(@PathVariable Long semesterId) {
        return ResponseEntity.ok(service.solveForSemester(semesterId));
    }

    @PostMapping("/async/{semesterId}")
    public ResponseEntity<?> solveAsync(@PathVariable Long semesterId) {
        return ResponseEntity.ok(service.startSolving(semesterId));
    }

    @GetMapping("/status/{semesterId}")
    public ResponseEntity<?> status(@PathVariable Long semesterId) {
        return ResponseEntity.ok(service.getStatus(semesterId));
    }

    @GetMapping("/result/{semesterId}")
    public ResponseEntity<?> result(@PathVariable Long semesterId) {
        return ResponseEntity.ok(service.getResult(semesterId));
    }

    @PostMapping("/terminate/{semesterId}")
    public ResponseEntity<?> stop(@PathVariable Long semesterId) {
        return ResponseEntity.ok(service.terminate(semesterId));
    }
}

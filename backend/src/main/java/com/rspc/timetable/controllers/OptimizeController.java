package com.rspc.timetable.controllers;

import com.rspc.timetable.optaplanner.TimetableOptaPlannerService;
import com.rspc.timetable.optaplanner.TimetableSolution;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/optimize")
@CrossOrigin(origins = "http://localhost:5173")
public class OptimizeController {

    private final TimetableOptaPlannerService optimizeService;

    public OptimizeController(TimetableOptaPlannerService optimizeService) {
        this.optimizeService = optimizeService;
    }

    @PostMapping("/start/{semesterId}")
    public ResponseEntity<String> start(@PathVariable Long semesterId) {
        Long jobId = optimizeService.startSolving(semesterId);
        return ResponseEntity.ok("Started solving. Job ID = " + jobId);
    }

    @GetMapping("/status/{semesterId}")
    public ResponseEntity<String> status(@PathVariable Long semesterId) {
        String status = optimizeService.getStatus(semesterId);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/terminate/{semesterId}")
    public ResponseEntity<String> terminate(@PathVariable Long semesterId) {
        boolean ok = optimizeService.terminate(semesterId);
        return ResponseEntity.ok(ok ? "Termination requested for " + semesterId
                : "Solver was not running for " + semesterId);
    }

    @GetMapping("/result/{semesterId}")
    public ResponseEntity<TimetableSolution> result(@PathVariable Long semesterId) {
        Optional<TimetableSolution> optional = optimizeService.getResult(semesterId);
        return optional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}

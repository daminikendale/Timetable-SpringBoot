package com.rspc.timetable.controllers;

import com.rspc.timetable.optaplanner.TimetableOptaPlannerService;
import com.rspc.timetable.optaplanner.TimetableSolution;
import com.rspc.timetable.services.TimeTableProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/optimize")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class OptimizeController {

    private final TimeTableProblemService problemService;
    private final TimetableOptaPlannerService solverService;

    @PostMapping("/run/{semesterId}")
    public ResponseEntity<?> run(@PathVariable Long semesterId) {

        Long jobId = solverService.startSolving(semesterId);

        Optional<TimetableSolution> result;
        int attempts = 0;

        do {
            try { Thread.sleep(500); } catch (Exception ignored) {}
            result = solverService.getResult(semesterId);
            attempts++;
        } while (result.isEmpty() && attempts < 20);

        if (result.isEmpty()) {
            return ResponseEntity.accepted().body("Solver still running... jobId = " + jobId);
        }

        TimetableSolution solved = result.get();
        problemService.saveSolution(solved, semesterId);

        return ResponseEntity.ok(solved);
    }
}

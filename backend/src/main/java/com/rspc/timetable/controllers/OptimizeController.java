package com.rspc.timetable.controllers;

import com.rspc.timetable.optaplanner.TimetableOptaPlannerService;
import com.rspc.timetable.optaplanner.TimetableSolution;
import com.rspc.timetable.services.TimeTableProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/optimize")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class OptimizeController {

    private final TimeTableProblemService problemService;
    private final TimetableOptaPlannerService solverService;

    @PostMapping("/run/{semesterId}")
    public ResponseEntity<TimetableSolution> run(@PathVariable Long semesterId) {
        TimetableSolution problem = problemService.load(semesterId);
        TimetableSolution solved = solverService.solve(problem);
        problemService.save(solved);
        return ResponseEntity.ok(solved);
    }
}

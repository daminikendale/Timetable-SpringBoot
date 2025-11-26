package com.rspc.timetable.controllers;

import com.rspc.timetable.services.TimetableGeneratorService;
import com.rspc.timetable.optaplanner.TimetableOptaPlannerService;
import com.rspc.timetable.optaplanner.TimetableSolution;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/timetable")
@CrossOrigin(origins = "http://localhost:5173")
public class TimetableController {

    private final TimetableGeneratorService generatorService;
    private final TimetableOptaPlannerService optaPlannerService;

    public TimetableController(TimetableGeneratorService generatorService,
                               TimetableOptaPlannerService optaPlannerService) {
        this.generatorService = generatorService;
        this.optaPlannerService = optaPlannerService;
    }

    /* --------------------------------------------------------
     *  BACKTRACKING GENERATOR
     * -------------------------------------------------------- */
    @PostMapping("/generate/{semesterId}")
    public ResponseEntity<Map<String, Object>> generateTimetable(@PathVariable Long semesterId) {
        boolean success = generatorService.generateTimetable(semesterId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message",
                success ? "Timetable generated successfully using backtracking algorithm"
                        : "Failed to generate timetable - check constraints or data");
        response.put("semesterId", semesterId);

        return ResponseEntity.ok(response);
    }

    /* --------------------------------------------------------
     *  OPTAPLANNER SOLVER
     * -------------------------------------------------------- */
    @PostMapping("/solve/{semesterId}")
    public ResponseEntity<Map<String, Object>> startOptaPlannerSolver(@PathVariable Long semesterId) {
        try {
            Long jobId = optaPlannerService.startSolving(semesterId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "OptaPlanner solver started");
            response.put("jobId", jobId);
            response.put("semesterId", semesterId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to start solver: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/solve/status/{semesterId}")
    public ResponseEntity<Map<String, Object>> getSolverStatus(@PathVariable Long semesterId) {
        String status = optaPlannerService.getStatus(semesterId);

        Map<String, Object> response = new HashMap<>();
        response.put("semesterId", semesterId);
        response.put("status", status);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/solve/result/{semesterId}")
    public ResponseEntity<?> getSolverResult(@PathVariable Long semesterId) {
        Optional<TimetableSolution> result = optaPlannerService.getResult(semesterId);

        if (result.isPresent()) {
            TimetableSolution solution = result.get();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("score", solution.getScore() != null ? solution.getScore().toString() : "N/A");
            response.put("classCount", solution.getPlannedClassList().size());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/solve/terminate/{semesterId}")
    public ResponseEntity<Map<String, Object>> terminateSolver(@PathVariable Long semesterId) {
        boolean terminated = optaPlannerService.terminate(semesterId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", terminated);
        response.put("message",
                terminated ? "Solver terminated" : "No running solver to terminate");
        response.put("semesterId", semesterId);

        return ResponseEntity.ok(response);
    }

    /* --------------------------------------------------------
     *  NEW: RUN FOR MULTIPLE SEMESTERS (ODD, EVEN, ALL)
     * -------------------------------------------------------- */

    @PostMapping("/solve/odd")
    public ResponseEntity<Map<String, Object>> solveOddSemesters() {
        List<Long> oddSems = List.of(1L, 3L, 5L, 7L);
        oddSems.forEach(optaPlannerService::startSolving);

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "OptaPlanner started for odd semesters");
        res.put("semesters", oddSems);

        return ResponseEntity.ok(res);
    }

    @PostMapping("/solve/even")
    public ResponseEntity<Map<String, Object>> solveEvenSemesters() {
        List<Long> evenSems = List.of(2L, 4L, 6L, 8L);
        evenSems.forEach(optaPlannerService::startSolving);

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "OptaPlanner started for even semesters");
        res.put("semesters", evenSems);

        return ResponseEntity.ok(res);
    }

    @PostMapping("/solve/all")
    public ResponseEntity<Map<String, Object>> solveAllSemesters() {
        List<Long> all = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L);
        all.forEach(optaPlannerService::startSolving);

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "OptaPlanner started for ALL semesters");
        res.put("semesters", all);

        return ResponseEntity.ok(res);
    }


    /* --------------------------------------------------------
     *  HEALTH CHECK
     * -------------------------------------------------------- */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Timetable Generation Service");
        return ResponseEntity.ok(response);
    }
}

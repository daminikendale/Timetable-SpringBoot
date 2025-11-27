package com.rspc.timetable.controllers;

import com.rspc.timetable.optaplanner.TimetableOptaPlannerService;
import com.rspc.timetable.optaplanner.TimetableSolution;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/timetable")
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableOptaPlannerService optaPlannerService;

    @PostMapping("/solve/{semesterId}")
    public ResponseEntity<?> solve(@PathVariable Long semesterId) {

        TimetableSolution solved = optaPlannerService.solveForSemester(semesterId);

        return ResponseEntity.ok(
                new java.util.HashMap<>() {{
                    put("success", true);
                    put("semesterId", semesterId);
                    put("classCount", solved.getPlannedClassList().size());
                    put("score", solved.getScore() != null ? solved.getScore().toString() : "N/A");
                    put("message", "Solved and saved successfully");
                }}
        );
    }

    @PostMapping("/solve/odd")
public ResponseEntity<Map<String, Object>> solveAllOddSemesters() {
    try {
        // Fetch all odd semester IDs from DB
        List<Long> oddSemIds = List.of(1L, 3L, 5L, 7L); // You can replace this with DB-based logic if needed

        Map<Long, Long> jobIds = new HashMap<>();

        for (Long semId : oddSemIds) {
            Long jobId = optaPlannerService.startSolving(semId);
            jobIds.put(semId, jobId);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "OptaPlanner started for all odd semesters");
        response.put("jobs", jobIds);

        return ResponseEntity.ok(response);

    } catch (Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Failed: " + ex.getMessage());
        return ResponseEntity.status(500).body(response);
    }
}

}

package com.rspc.timetable.controllers;

import com.rspc.timetable.entities.Break;
import com.rspc.timetable.services.BreakService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/breaks")
public class BreakController {

    private final BreakService breakService;

    public BreakController(BreakService breakService) {
        this.breakService = breakService;
    }

    // Bulk POST: Create multiple breaks at once
    @PostMapping("/bulk")
    public List<Break> createBreaks(@RequestBody List<Break> breaks) {
        return breakService.saveAll(breaks);
    }

    // GET all breaks
    @GetMapping
    public List<Break> getAllBreaks() {
        return breakService.getAll();
    }
}

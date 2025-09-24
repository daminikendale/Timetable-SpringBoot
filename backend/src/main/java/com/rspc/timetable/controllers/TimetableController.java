// src/main/java/com/rspc/timetable/controllers/TimetableController.java
package com.rspc.timetable.controllers;

import com.rspc.timetable.services.TimetableGeneratorService;
import com.rspc.timetable.services.TimetableService;
import com.rspc.timetable.entities.Timetable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timetable")
public class TimetableController {

    private final TimetableGeneratorService generatorService;
    private final TimetableService timetableService;

    public TimetableController(TimetableGeneratorService generatorService,
                               TimetableService timetableService) {
        this.generatorService = generatorService;
        this.timetableService = timetableService;
    }

    @PostMapping("/generate")
    public String generate() {
        // Note: method name is generateCompleteTimetable()
        return generatorService.generateCompleteTimetable();
    }

    @GetMapping
    public List<Timetable> all() {
        return timetableService.getAllTimetables();
    }
}

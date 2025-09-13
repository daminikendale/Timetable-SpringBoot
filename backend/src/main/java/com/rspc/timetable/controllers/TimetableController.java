package com.rspc.timetable.controllers;

import com.rspc.timetable.entities.Timetable;
import com.rspc.timetable.services.TimetableService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/timetables")
public class TimetableController {

    private final TimetableService timetableService;

    public TimetableController(TimetableService timetableService) {
        this.timetableService = timetableService;
    }

    @PostMapping
    public Timetable createTimetable(@RequestBody Timetable timetable) {
        return timetableService.saveTimetable(timetable);
    }

    @PostMapping("/bulk")
    public List<Timetable> createBulkTimetables(@RequestBody List<Timetable> timetables) {
        return timetableService.saveBulk(timetables);
    }

    @GetMapping
    public List<Timetable> getAllTimetables() {
        return timetableService.getAllTimetables();
    }

    @GetMapping("/{id}")
    public Timetable getTimetableById(@PathVariable Long id) {
        return timetableService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteTimetable(@PathVariable Long id) {
        timetableService.delete(id);
    }

    @GetMapping("/division/{divisionId}/date/{date}")
    public List<Timetable> getTimetableForDivisionOnDate(
            @PathVariable Long divisionId,
            @PathVariable String date
    ) {
        LocalDate localDate = LocalDate.parse(date);
        return timetableService.getTimetableForDivisionOnDate(divisionId, localDate);
    }
}

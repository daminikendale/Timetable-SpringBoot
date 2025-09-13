// src/main/java/com/rspc/timetable/controllers/TimetableChangeController.java
package com.rspc.timetable.controllers;

import com.rspc.timetable.dto.TimetableChangeRequest;
import com.rspc.timetable.entities.Timetable;
import com.rspc.timetable.services.TimetableChangeService;
import com.rspc.timetable.services.TimetableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/timetable-changes")
public class TimetableChangeController {

    private final TimetableChangeService changeService;
    private final TimetableService timetableService;

    public TimetableChangeController(TimetableChangeService changeService, TimetableService timetableService) {
        this.changeService = changeService;
        this.timetableService = timetableService;
    }

    @PostMapping
    public ResponseEntity<List<Timetable>> apply(@RequestBody TimetableChangeRequest request) {
        return ResponseEntity.ok(changeService.applyChange(request));
    }

    // Useful for the UI to verify the effective TT for a date after a TEMPORARY change
    @GetMapping("/effective")
    public ResponseEntity<List<Timetable>> getEffective(
            @RequestParam Long divisionId,
            @RequestParam String date // ISO yyyy-MM-dd
    ) {
        LocalDate d = LocalDate.parse(date);
        return ResponseEntity.ok(timetableService.getTimetableForDivisionOnDate(divisionId, d));
    }
}

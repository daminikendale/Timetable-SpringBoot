package com.rspc.timetable.controllers;

import com.rspc.timetable.dto.ScheduledClassDTO;
import com.rspc.timetable.services.ScheduledClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/timetable")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ScheduledClassController {

    private final ScheduledClassService scheduledClassService;

    @GetMapping("/by-division/{divisionId}")
    public ResponseEntity<List<ScheduledClassDTO>> getTimetableForDivision(@PathVariable Long divisionId) {
        return ResponseEntity.ok(scheduledClassService.getTimetableForDivision(divisionId));
    }

    @GetMapping("/by-teacher/{teacherId}")
    public ResponseEntity<List<ScheduledClassDTO>> getTimetableForTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(scheduledClassService.getTimetableForTeacher(teacherId));
    }
}

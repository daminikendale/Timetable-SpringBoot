package com.rspc.timetable.controllers;

import com.rspc.timetable.dto.ScheduledClassDTO;
import com.rspc.timetable.services.ScheduledClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scheduled-classes")
@RequiredArgsConstructor
public class ScheduledClassController {

    private final ScheduledClassService scheduledClassService;

    @GetMapping("/division/{divisionId}/timetable")
    public List<ScheduledClassDTO> getTimetableForDivision(@PathVariable Long divisionId) {
        return scheduledClassService.getTimetableForDivision(divisionId);
    }

    @GetMapping("/teacher/{teacherId}/timetable")
    public List<ScheduledClassDTO> getTimetableForTeacher(@PathVariable Long teacherId) {
        return scheduledClassService.getTimetableForTeacher(teacherId);
    }
}

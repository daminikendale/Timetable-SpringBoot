package com.rspc.timetable.controllers;

import com.rspc.timetable.dto.TeacherSubjectAllocationDTO;
import com.rspc.timetable.entities.TeacherSubjectAllocation;
import com.rspc.timetable.services.TeacherSubjectAllocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher-allocations")
public class TeacherSubjectAllocationController {

    private final TeacherSubjectAllocationService service;

    public TeacherSubjectAllocationController(TeacherSubjectAllocationService service) {
        this.service = service;
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<TeacherSubjectAllocation>> createBulk(
            @RequestBody List<TeacherSubjectAllocationDTO> dtos) {

        List<TeacherSubjectAllocation> savedAllocations = service.saveAll(dtos);
        return ResponseEntity.ok(savedAllocations);
    }
}

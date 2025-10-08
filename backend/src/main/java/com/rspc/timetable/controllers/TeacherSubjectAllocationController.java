package com.rspc.timetable.controllers;

import com.rspc.timetable.dto.TeacherSubjectAllocationDTO;
import com.rspc.timetable.entities.TeacherSubjectAllocation;
import com.rspc.timetable.repositories.TeacherSubjectAllocationRepository;
import com.rspc.timetable.services.AllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/allocations")
@CrossOrigin(origins = "http://localhost:5173")
public class TeacherSubjectAllocationController {

    @Autowired
    private AllocationService allocationService;

    @Autowired
    private TeacherSubjectAllocationRepository allocationRepository;

    // ✅ Create a single teacher-to-subject allocation
    @PostMapping("/create")
    public ResponseEntity<TeacherSubjectAllocationDTO> createAllocation(@RequestBody TeacherSubjectAllocationDTO allocationDTO) {
        try {
            TeacherSubjectAllocationDTO createdAllocation = allocationService.createAllocation(allocationDTO);
            return ResponseEntity.ok(createdAllocation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // ✅ Create multiple allocations
    @PostMapping("/bulk")
    public ResponseEntity<List<TeacherSubjectAllocationDTO>> createBulkAllocations(@RequestBody List<TeacherSubjectAllocationDTO> allocationDTOs) {
        try {
            List<TeacherSubjectAllocationDTO> createdAllocations = allocationService.createBulkAllocations(allocationDTOs);
            return ResponseEntity.ok(createdAllocations);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // ✅ Create allocations from subject → teacher list map
    @PostMapping("/bulk-map-create")
    public ResponseEntity<List<TeacherSubjectAllocationDTO>> createAllocationsFromMap(@RequestBody Map<Long, List<Long>> subjectToTeachersMap) {
        try {
            List<TeacherSubjectAllocationDTO> createdAllocations = allocationService.createAllocationsFromMap(subjectToTeachersMap);
            return ResponseEntity.ok(createdAllocations);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // ✅ Get all allocations
    @GetMapping
    public ResponseEntity<List<TeacherSubjectAllocationDTO>> getAllAllocations() {
        return ResponseEntity.ok(allocationService.getAllAllocations());
    }

    // ✅ Get allocations by teacher
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<TeacherSubjectAllocationDTO>> getAllocationsByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(allocationService.getAllocationsByTeacher(teacherId));
    }

    // ✅ Get allocations by subject
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<TeacherSubjectAllocationDTO>> getAllocationsBySubject(@PathVariable Long subjectId) {
        return ResponseEntity.ok(allocationService.getAllocationsBySubject(subjectId));
    }

    // ✅ Bulk update priorities (fixed ByteBuddy error)
    @PutMapping("/bulk-update")
    public ResponseEntity<?> bulkUpdateAllocations(@RequestBody List<TeacherSubjectAllocationDTO> allocationDTOs) {
        try {
            List<TeacherSubjectAllocation> updatedEntities = new ArrayList<>();

            for (TeacherSubjectAllocationDTO dto : allocationDTOs) {
                Optional<TeacherSubjectAllocation> opt = allocationRepository.findById(dto.getId());
                if (opt.isPresent()) {
                    TeacherSubjectAllocation allocation = opt.get();
                    allocation.setPriority(dto.getPriority());
                    updatedEntities.add(allocationRepository.save(allocation));
                }
            }

            // ✅ Convert entities to DTOs to avoid Hibernate proxy serialization issues
            List<TeacherSubjectAllocationDTO> updatedDTOs = updatedEntities.stream()
                    .map(a -> new TeacherSubjectAllocationDTO(
                            a.getId(),
                            a.getTeacher().getId(),
                            a.getSubject().getId(),
                            a.getPriority()
                    ))
                    .toList();

            return ResponseEntity.ok(updatedDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating allocations: " + e.getMessage());
        }
    }
}

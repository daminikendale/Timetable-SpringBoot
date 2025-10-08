package com.rspc.timetable.controllers;

import com.rspc.timetable.dto.CourseOfferingDTO;
import com.rspc.timetable.services.CourseOfferingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course-offerings")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class CourseOfferingController {

    private final CourseOfferingService courseOfferingService;

    @PostMapping
    public ResponseEntity<CourseOfferingDTO> createCourseOffering(@RequestBody CourseOfferingDTO dto) {
        CourseOfferingDTO createdOffering = courseOfferingService.createCourseOffering(dto);
        return new ResponseEntity<>(createdOffering, HttpStatus.CREATED);
    }

    // ✅ New Bulk Insert Endpoint
    @PostMapping("/bulk")
    public ResponseEntity<List<CourseOfferingDTO>> createCourseOfferings(@RequestBody List<CourseOfferingDTO> offerings) {
        // FIX: Renamed the method call to match the service interface
        List<CourseOfferingDTO> createdOfferings = courseOfferingService.createBulkCourseOfferings(offerings);
        return new ResponseEntity<>(createdOfferings, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CourseOfferingDTO>> getAllCourseOfferings() {
        return ResponseEntity.ok(courseOfferingService.getAllOfferings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseOfferingDTO> getCourseOfferingById(@PathVariable Long id) {
        return ResponseEntity.ok(courseOfferingService.getOfferingById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourseOffering(@PathVariable Long id) {
        courseOfferingService.deleteOffering(id);
        return ResponseEntity.noContent().build();
    }
}

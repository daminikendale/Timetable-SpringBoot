package com.rspc.timetable.controllers;

import com.rspc.timetable.dto.TeacherDTO;
import com.rspc.timetable.services.TeacherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    // GET /api/teachers - Get all teachers
    @GetMapping
    public ResponseEntity<List<TeacherDTO>> getAllTeachers() {
        List<TeacherDTO> teachers = teacherService.getAllTeachers();
        return ResponseEntity.ok(teachers);
    }

    // GET /api/teachers/{id} - Get a single teacher by ID
    @GetMapping("/{id}")
    public ResponseEntity<TeacherDTO> getTeacherById(@PathVariable Long id) {
        TeacherDTO teacher = teacherService.getTeacherById(id);
        return ResponseEntity.ok(teacher);
    }

    // POST /api/teachers - Create a new teacher
    @PostMapping
    public ResponseEntity<TeacherDTO> createTeacher(@RequestBody TeacherDTO teacherDTO) {
        TeacherDTO createdTeacher = teacherService.createTeacher(teacherDTO);
        return new ResponseEntity<>(createdTeacher, HttpStatus.CREATED);
    }

    // PUT /api/teachers/{id} - Update an existing teacher
    @PutMapping("/{id}")
    public ResponseEntity<TeacherDTO> updateTeacher(@PathVariable Long id, @RequestBody TeacherDTO teacherDTO) {
        TeacherDTO updatedTeacher = teacherService.updateTeacher(id, teacherDTO);
        return ResponseEntity.ok(updatedTeacher);
    }

    // DELETE /api/teachers/{id} - Delete a teacher
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }

    // POST /api/teachers/bulk - Bulk create teachers from a list of DTOs
    @PostMapping("/bulk")
    public ResponseEntity<List<TeacherDTO>> createTeachersFromDTOs(@RequestBody List<TeacherDTO> teacherDTOs) {
        List<TeacherDTO> createdTeachers = teacherService.saveAllTeachersFromDTOs(teacherDTOs);
        return new ResponseEntity<>(createdTeachers, HttpStatus.CREATED);
    }
}

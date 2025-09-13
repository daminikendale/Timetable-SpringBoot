package com.rspc.timetable.controllers;

import com.rspc.timetable.entities.Teacher;
import com.rspc.timetable.services.TeacherService;
import com.rspc.timetable.dto.TeacherDTO;
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

    @GetMapping
    public List<Teacher> getAllTeachers() {
        return teacherService.getAllTeachers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Teacher> getTeacherById(@PathVariable Long id) {
        return teacherService.getTeacherById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Teacher createTeacher(@RequestBody Teacher teacher) {
        return teacherService.saveTeacher(teacher);
    }

    @PostMapping("/bulk")
    public List<Teacher> createTeachers(@RequestBody List<Teacher> teachers) {
        return teacherService.saveAllTeachers(teachers);
    }

    @PostMapping("/bulk-dto")
    public List<Teacher> createTeachersFromDTOs(@RequestBody List<TeacherDTO> teacherDTOs) {
        return teacherService.saveAllTeachersFromDTOs(teacherDTOs);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dto")
    public List<TeacherDTO> getAllTeacherDTOs() {
        return teacherService.getAllTeacherDTOs();
    }
}

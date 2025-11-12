package com.rspc.timetable.controllers;

import com.rspc.timetable.dto.ClassroomDTO;
import com.rspc.timetable.entities.Classroom;
import com.rspc.timetable.services.ClassroomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classrooms")
public class ClassroomController {

    private final ClassroomService classroomService;

    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }

    // ----- Entity endpoints -----

    @GetMapping
    public List<Classroom> getAllClassrooms() {
        return classroomService.getAllClassrooms();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Classroom> getClassroomById(@PathVariable Long id) {
        return classroomService.getClassroomById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Classroom createClassroom(@RequestBody Classroom classroom) {
        return classroomService.saveClassroom(classroom);
    }

    @PostMapping("/bulk")
    public List<Classroom> createClassrooms(@RequestBody List<Classroom> classrooms) {
        return classroomService.saveAllClassrooms(classrooms);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassroom(@PathVariable Long id) {
        classroomService.deleteClassroom(id);
        return ResponseEntity.noContent().build();
    }

    // ----- DTO endpoints -----

    @GetMapping("/dto")
    public ResponseEntity<List<ClassroomDTO>> getAllClassroomDTOs() {
        return ResponseEntity.ok(classroomService.getAllClassroomDTOs());
    }

    @GetMapping("/dto/{id}")
    public ResponseEntity<ClassroomDTO> getClassroomDTOById(@PathVariable Long id) {
        return classroomService.getClassroomDTOById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

package com.rspc.timetable.controllers;

import com.rspc.timetable.entities.Tutorial;
import com.rspc.timetable.services.TutorialService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tutorials")
public class TutorialController {

    private final TutorialService service;

    public TutorialController(TutorialService service) {
        this.service = service;
    }

    @GetMapping
    public List<Tutorial> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tutorial> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Tutorial create(@RequestBody Tutorial tutorial) {
        return service.save(tutorial);
    }

    @PostMapping("/bulk")
    public List<Tutorial> createBulk(@RequestBody List<Tutorial> tutorials) {
        return service.saveAll(tutorials);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tutorial> update(@PathVariable Long id, @RequestBody Tutorial updated) {
        return service.getById(id)
                .map(existing -> {
                    existing.setSubject(updated.getSubject());
                    existing.setDivision(updated.getDivision());
                    existing.setClassroom(updated.getClassroom());
                    existing.setDurationInHours(updated.getDurationInHours());
                    existing.setTeacher(updated.getTeacher());
                    return ResponseEntity.ok(service.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

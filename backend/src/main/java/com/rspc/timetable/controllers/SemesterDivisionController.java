package com.rspc.timetable.controllers;

import com.rspc.timetable.dto.SemesterDivisionDTO;
import com.rspc.timetable.services.SemesterDivisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/semester-divisions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class SemesterDivisionController {

    private final SemesterDivisionService semesterDivisionService;

    @PostMapping
    public ResponseEntity<SemesterDivisionDTO> createMapping(@RequestBody SemesterDivisionDTO dto) {
        SemesterDivisionDTO createdMapping = semesterDivisionService.createSemesterDivision(dto);
        return new ResponseEntity<>(createdMapping, HttpStatus.CREATED);
    }

    @GetMapping("/by-semester/{semesterId}")
    public ResponseEntity<List<SemesterDivisionDTO>> getMappingsForSemester(@PathVariable Long semesterId) {
        return ResponseEntity.ok(semesterDivisionService.getDivisionsForSemester(semesterId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMapping(@PathVariable Long id) {
        semesterDivisionService.deleteSemesterDivision(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk")
public ResponseEntity<List<SemesterDivisionDTO>> createBulkMappings(@RequestBody List<SemesterDivisionDTO> dtos) {
    List<SemesterDivisionDTO> createdMappings = semesterDivisionService.createSemesterDivisions(dtos);
    return new ResponseEntity<>(createdMappings, HttpStatus.CREATED);
}

}

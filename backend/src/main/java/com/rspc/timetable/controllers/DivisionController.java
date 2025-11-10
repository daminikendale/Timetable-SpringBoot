package com.rspc.timetable.controllers;

import com.rspc.timetable.dto.DivisionDTO;
import com.rspc.timetable.services.DivisionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/divisions")
@CrossOrigin(origins = "http://localhost:5173")
public class DivisionController {

    private final DivisionService divisionService;

    public DivisionController(DivisionService divisionService) {
        this.divisionService = divisionService;
    }

    // UPDATED METHOD: Now accepts yearId parameter to filter divisions
    @GetMapping
    public ResponseEntity<List<DivisionDTO>> getAllDivisions(@RequestParam(required = false) Long yearId) {
        if (yearId != null) {
            // If yearId is provided, return divisions for that year only
            return ResponseEntity.ok(divisionService.getDivisionsByYearId(yearId));
        } else {
            // If no yearId, return all divisions
            return ResponseEntity.ok(divisionService.getAllDivisions());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DivisionDTO> getDivisionById(@PathVariable Long id) {
        return ResponseEntity.ok(divisionService.getDivisionById(id));
    }

    @PostMapping
    public ResponseEntity<DivisionDTO> createDivision(@RequestBody DivisionDTO divisionDTO) {
        DivisionDTO createdDivision = divisionService.createDivision(divisionDTO);
        return new ResponseEntity<>(createdDivision, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DivisionDTO> updateDivision(@PathVariable Long id, @RequestBody DivisionDTO divisionDTO) {
        DivisionDTO updatedDivision = divisionService.updateDivision(id, divisionDTO);
        return ResponseEntity.ok(updatedDivision);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDivision(@PathVariable Long id) {
        divisionService.deleteDivision(id);
        return ResponseEntity.noContent().build();
    }
}

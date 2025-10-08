package com.rspc.timetable.controllers;

import com.rspc.timetable.dto.DivisionDTO;
import com.rspc.timetable.services.DivisionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/divisions")
@CrossOrigin(origins = "http://localhost:5173") // Enables CORS for your React frontend
public class DivisionController {

    private final DivisionService divisionService;

    public DivisionController(DivisionService divisionService) {
        this.divisionService = divisionService;
    }

    // GET /api/divisions - Get all divisions
    @GetMapping
    public ResponseEntity<List<DivisionDTO>> getAllDivisions() {
        return ResponseEntity.ok(divisionService.getAllDivisions());
    }

    // GET /api/divisions/{id} - Get a single division by ID
    @GetMapping("/{id}")
    public ResponseEntity<DivisionDTO> getDivisionById(@PathVariable Long id) {
        DivisionDTO division = divisionService.getDivisionById(id);
        return ResponseEntity.ok(division);
    }

    // POST /api/divisions - Create a new division
    @PostMapping
    public ResponseEntity<DivisionDTO> createDivision(@RequestBody DivisionDTO divisionDTO) {
        DivisionDTO createdDivision = divisionService.createDivision(divisionDTO);
        return new ResponseEntity<>(createdDivision, HttpStatus.CREATED);
    }
    
    // PUT /api/divisions/{id} - Update an existing division
    @PutMapping("/{id}")
    public ResponseEntity<DivisionDTO> updateDivision(@PathVariable Long id, @RequestBody DivisionDTO divisionDTO) {
        DivisionDTO updatedDivision = divisionService.updateDivision(id, divisionDTO);
        return ResponseEntity.ok(updatedDivision);
    }

    // DELETE /api/divisions/{id} - Delete a division
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDivision(@PathVariable Long id) {
        divisionService.deleteDivision(id);
        return ResponseEntity.noContent().build();
    }

    // POST /api/divisions/bulk - Bulk create divisions
    @PostMapping("/bulk")
    public ResponseEntity<List<DivisionDTO>> createDivisionsBulk(@RequestBody List<DivisionDTO> divisionDTOs) {
        List<DivisionDTO> savedDivisions = divisionService.saveAllDivisions(divisionDTOs);
        return new ResponseEntity<>(savedDivisions, HttpStatus.CREATED);
    }
}

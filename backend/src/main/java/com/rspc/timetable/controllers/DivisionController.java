package com.rspc.timetable.controllers;

import com.rspc.timetable.dto.DivisionDTO;
import com.rspc.timetable.entities.Division;
import com.rspc.timetable.services.DivisionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/divisions")
public class DivisionController {

    private final DivisionService divisionService;

    public DivisionController(DivisionService divisionService) {
        this.divisionService = divisionService;
    }

    @GetMapping
    public List<DivisionDTO> getAllDivisions() {
        return divisionService.getAllDivisionDTOs();
    }

    @GetMapping("/{id}")
    public Division getById(@PathVariable Long id) {
        return divisionService.getDivisionById(id).orElse(null);
    }

    @PostMapping
    public Division createDivision(@RequestBody Division division) {
        return divisionService.saveDivision(division);
    }

    @PostMapping("/bulk")
    public List<Division> createDivisions(@RequestBody List<Division> divisions) {
        return divisionService.saveAllDivisions(divisions);
    }

}

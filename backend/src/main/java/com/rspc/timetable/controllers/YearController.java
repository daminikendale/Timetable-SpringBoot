package com.rspc.timetable.controllers;

import com.rspc.timetable.entities.Year;
import com.rspc.timetable.services.YearService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.rspc.timetable.dto.YearDTO;
import java.util.List;

@RestController
@RequestMapping("/api/years")
@CrossOrigin(origins = "http://localhost:5173")  // ADD THIS LINE TO ALLOW REACT FRONTEND
public class YearController {

    private final YearService yearService;

    public YearController(YearService yearService) {
        this.yearService = yearService;
    }

    @GetMapping
    public List<Year> getAllYears() {
        return yearService.getAllYears();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Year> getYearById(@PathVariable Long id) {
        return yearService.getYearById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/bulk")
    public List<Year> createYears(@RequestBody List<Year> years) {
        return yearService.saveYears(years);
    }

    @PostMapping
    public Year createYear(@RequestBody Year year) {
        return yearService.saveYear(year);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteYear(@PathVariable Long id) {
        yearService.deleteYear(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dto")
    public List<YearDTO> getAllYearDTOs() {
        return yearService.getAllYearDTOs();
    }
}

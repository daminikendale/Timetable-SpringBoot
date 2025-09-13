package com.rspc.timetable.controllers;

import com.rspc.timetable.entities.WorkingHours;
import com.rspc.timetable.repositories.WorkingHoursRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/working-hours")
public class WorkingHoursController {

    @Autowired
    private WorkingHoursRepository workingHoursRepository;

    @PostMapping
    public WorkingHours createWorkingHours(@RequestBody WorkingHours workingHours) {
        return workingHoursRepository.save(workingHours);
    }

    @GetMapping
    public List<WorkingHours> getAllWorkingHours() {
        return workingHoursRepository.findAll();
    }

    @GetMapping("/{id}")
    public WorkingHours getWorkingHoursById(@PathVariable Long id) {
        return workingHoursRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public WorkingHours updateWorkingHours(@PathVariable Long id, @RequestBody WorkingHours updated) {
        return workingHoursRepository.findById(id)
                .map(existing -> {
                    existing.setStartTime(updated.getStartTime());
                    existing.setEndTime(updated.getEndTime());
                    return workingHoursRepository.save(existing);
                })
                .orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deleteWorkingHours(@PathVariable Long id) {
        workingHoursRepository.deleteById(id);
    }
}

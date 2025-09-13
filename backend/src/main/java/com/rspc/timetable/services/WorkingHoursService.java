package com.rspc.timetable.services;

import com.rspc.timetable.entities.WorkingHours;
import com.rspc.timetable.repositories.WorkingHoursRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WorkingHoursService {
    private final WorkingHoursRepository workingHoursRepository;

    public WorkingHoursService(WorkingHoursRepository workingHoursRepository) {
        this.workingHoursRepository = workingHoursRepository;
    }

    public List<WorkingHours> getAllWorkingHours() {
        return workingHoursRepository.findAll();
    }

    public Optional<WorkingHours> getById(Long id) {
        return workingHoursRepository.findById(id);
    }

    public WorkingHours addWorkingHours(WorkingHours workingHours) {
        return workingHoursRepository.save(workingHours);
    }

    public WorkingHours updateWorkingHours(Long id, WorkingHours updated) {
        return workingHoursRepository.findById(id)
                .map(existing -> {
                    existing.setStartTime(updated.getStartTime());
                    existing.setEndTime(updated.getEndTime());
                    return workingHoursRepository.save(existing);
                })
                .orElse(null);
    }

    public void deleteWorkingHours(Long id) {
        workingHoursRepository.deleteById(id);
    }
}

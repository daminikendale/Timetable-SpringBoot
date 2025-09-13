package com.rspc.timetable.services;

import com.rspc.timetable.entities.ElectiveAllocation;
import com.rspc.timetable.entities.SubjectCategory;
import com.rspc.timetable.repositories.ElectiveAllocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ElectiveAllocationService {

    private final ElectiveAllocationRepository repository;

    public ElectiveAllocationService(ElectiveAllocationRepository repository) {
        this.repository = repository;
    }

    public List<ElectiveAllocation> getAll() {
        return repository.findAll();
    }

    public ElectiveAllocation save(ElectiveAllocation allocation) {
        // Only allow PROGRAM or OPEN electives
        if(allocation.getSubject().getCategory() == SubjectCategory.REGULAR) {
            throw new IllegalArgumentException("Cannot allocate a regular subject as elective");
        }
        return repository.save(allocation);
    }

    public List<ElectiveAllocation> saveAll(List<ElectiveAllocation> allocations) {
        allocations.forEach(a -> {
            if(a.getSubject().getCategory() == SubjectCategory.REGULAR) {
                throw new IllegalArgumentException("Cannot allocate a regular subject as elective");
            }
        });
        return repository.saveAll(allocations);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}

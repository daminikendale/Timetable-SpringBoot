package com.rspc.timetable.services;

import com.rspc.timetable.entities.ElectiveGroup;
import com.rspc.timetable.repositories.ElectiveGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ElectiveGroupService {

    private final ElectiveGroupRepository electiveGroupRepository;

    /**
     * Retrieves all elective groups from the database.
     * @return a list of all ElectiveGroup entities.
     */
    public List<ElectiveGroup> getAll() {
        return electiveGroupRepository.findAll();
    }

    /**
     * Finds a single elective group by its ID.
     * @param id The ID of the elective group.
     * @return The found ElectiveGroup.
     * @throws NoSuchElementException if no group is found with the given ID.
     */
    public ElectiveGroup findById(Long id) {
        return electiveGroupRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ElectiveGroup not found with id: " + id));
    }

    /**
     * Saves a single ElectiveGroup entity.
     * @param electiveGroup The entity to save.
     * @return The saved entity.
     */
    public ElectiveGroup save(ElectiveGroup electiveGroup) {
        return electiveGroupRepository.save(electiveGroup);
    }
}

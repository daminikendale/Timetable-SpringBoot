package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.Division;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DivisionRepository extends JpaRepository<Division, Long> {

    /**
     * This is the method you need to add.
     * Spring Data JPA will automatically create a query that finds a Division
     * by its 'divisionName' field.
     * We return an Optional to gracefully handle cases where no division is found.
     */
    Optional<Division> findByDivisionName(String divisionName);

}

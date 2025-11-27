package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.Division;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DivisionRepository extends JpaRepository<Division, Long> {

    Optional<Division> findByDivisionName(String divisionName);

    // No findByYearId / findBySemesterId, because Division has no such fields
}

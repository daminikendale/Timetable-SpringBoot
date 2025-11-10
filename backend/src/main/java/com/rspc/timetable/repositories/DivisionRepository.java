package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.Division;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DivisionRepository extends JpaRepository<Division, Long> {
    
    Optional<Division> findByDivisionName(String divisionName);
    
    // ✅ CORRECT QUERY - Based on your working SQL
    @Query("SELECT DISTINCT d FROM Division d " +
           "JOIN SemesterDivision sd ON sd.division.id = d.id " +
           "JOIN sd.semester s " +
           "WHERE s.year.id = :yearId")
    List<Division> findByYearId(@Param("yearId") Long yearId);
}

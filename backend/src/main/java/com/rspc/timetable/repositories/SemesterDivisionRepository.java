package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.SemesterDivision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SemesterDivisionRepository extends JpaRepository<SemesterDivision, Long> {

    // --- **ADD THIS METHOD** ---
    // This declaration allows Spring Data JPA to create the query automatically.
    // It will find all SemesterDivision entries where the 'semesterNumber' of the associated 'Semester'
    // is in the provided list of integers.
    List<SemesterDivision> findBySemester_SemesterNumberIn(List<Integer> semesterNumbers);
    
    // You may also have other methods here, like:
    List<SemesterDivision> findBySemester_SemesterNumber(int semesterNumber);
    List<SemesterDivision> findBySemesterId(Long semesterId);
     List<SemesterDivision> findBySemester_IdOrderByDivision_Id(Long semesterId);
}

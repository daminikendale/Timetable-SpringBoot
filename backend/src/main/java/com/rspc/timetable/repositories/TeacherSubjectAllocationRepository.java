package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.Subject;
import com.rspc.timetable.entities.TeacherSubjectAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherSubjectAllocationRepository extends JpaRepository<TeacherSubjectAllocation, Long> {

    /**
     * Finds all teacher allocations for a given subject.
     * Used by the SubstitutionService to find qualified substitutes.
     */
    List<TeacherSubjectAllocation> findAllBySubject(Subject subject);

    /**
     * ✅ ADD THIS METHOD:
     * Finds all allocations for a specific subject by its ID.
     */
    List<TeacherSubjectAllocation> findBySubjectId(Long subjectId);

    /**
     * ✅ ADD THIS METHOD:
     * Finds all allocations for a specific teacher by their ID.
     */
    List<TeacherSubjectAllocation> findByTeacherId(Long teacherId);
}

package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.TeacherSubjectAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherSubjectAllocationRepository extends JpaRepository<TeacherSubjectAllocation, Long> {

    // Find all allocations for a given teacher's ID
    List<TeacherSubjectAllocation> findByTeacherId(Long teacherId);

    // Find all allocations for a given subject's ID
    List<TeacherSubjectAllocation> findBySubjectId(Long subjectId);
}

package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.TeacherSubjectAllocation;
import com.rspc.timetable.entities.TeacherSubjectAllocation.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeacherSubjectAllocationRepository extends JpaRepository<TeacherSubjectAllocation, Long> {
    List<TeacherSubjectAllocation> findBySubjectId(Long subjectId);
    List<TeacherSubjectAllocation> findBySubjectIdAndYearId(Long subjectId, Long yearId);
    List<TeacherSubjectAllocation> findBySubjectIdAndYearIdAndRole(Long subjectId, Long yearId, Role role);
}

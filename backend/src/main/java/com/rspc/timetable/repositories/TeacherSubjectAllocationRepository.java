package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.TeacherSubjectAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherSubjectAllocationRepository extends JpaRepository<TeacherSubjectAllocation, Long> {
}

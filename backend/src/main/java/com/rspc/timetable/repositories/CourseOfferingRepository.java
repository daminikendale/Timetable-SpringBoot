package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.CourseOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseOfferingRepository extends JpaRepository<CourseOffering, Long> {
    List<CourseOffering> findBySemesterId(Long semesterId);
    List<CourseOffering> findBySubjectId(Long subjectId);
    List<CourseOffering> findBySemester_IdAndSubject_Id(Long semesterId, Long subjectId);
}

package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.CourseOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseOfferingRepository extends JpaRepository<CourseOffering, Long> {
  boolean existsBySubjectIdAndYearIdAndSemesterId(Long subjectId, Long yearId, Long semesterId);
  List<CourseOffering> findByYearIdAndSemesterId(Long yearId, Long semesterId);
}

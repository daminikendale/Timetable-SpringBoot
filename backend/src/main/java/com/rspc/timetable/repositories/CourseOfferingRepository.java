package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.CourseOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseOfferingRepository extends JpaRepository<CourseOffering, Long> {

    /**
     * This is the correct method signature. It tells Spring Data JPA to find a
     * CourseOffering by navigating through its 'subject' field, then to the
     * 'semester' field within the Subject entity.
     */
    List<CourseOffering> findBySubject_Semester_SemesterNumberIn(List<Integer> semesterNumbers);

    // --- DELETE THIS LINE ---
    // List<CourseOffering> findBySemester_SemesterNumberIn(List<Integer> semesterNumbers);
}

package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.CourseOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseOfferingRepository extends JpaRepository<CourseOffering, Long> {

    // This method finds offerings by traversing through Subject to Semester
    List<CourseOffering> findBySubject_Semester_SemesterNumberIn(List<Integer> semesterNumbers);

    /**
     * ✅ THIS IS THE CORRECT METHOD NAME
     * Spring Data JPA will understand this and generate the correct query.
     * It finds offerings by the 'subjectId' AND by the 'semesterNumber' inside the 'semester' object.
     */
    List<CourseOffering> findBySubjectIdAndSemester_SemesterNumberIn(Long subjectId, List<Integer> semesterNumbers);
}

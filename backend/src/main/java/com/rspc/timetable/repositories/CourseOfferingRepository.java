package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.CourseOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseOfferingRepository extends JpaRepository<CourseOffering, Long> {

    // Already used by your service implementation
    List<CourseOffering> findBySubject_Semester_SemesterNumberIn(List<Integer> semesterNumbers);

    // ✅ Add this missing method (for single semester)
    List<CourseOffering> findBySubject_Semester_SemesterNumber(int semesterNumber);

    // ✅ Already used elsewhere for combined filtering
    List<CourseOffering> findBySubjectIdAndSemester_SemesterNumberIn(Long subjectId, List<Integer> semesterNumbers);
    
    List<CourseOffering> findBySemester_Id(Long semesterId);

}

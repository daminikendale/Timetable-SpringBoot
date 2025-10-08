package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.CourseOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseOfferingRepository extends JpaRepository<CourseOffering, Long> {

    /**
     * ✅ Custom JPQL query to find offerings by a list of semester numbers.
     * This query traverses through CourseOffering -> Subject -> Semester.
     */
    @Query("SELECT co FROM CourseOffering co WHERE co.subject.semester.semesterNumber IN :semesterNumbers")
    List<CourseOffering> findOfferingsBySemesterNumbers(@Param("semesterNumbers") List<Integer> semesterNumbers);

    /**
     * ✅ Custom JPQL query to find offerings by a subject ID and a list of semester numbers.
     */
    @Query("SELECT co FROM CourseOffering co WHERE co.subject.id = :subjectId AND co.subject.semester.semesterNumber IN :semesterNumbers")
    List<CourseOffering> findOfferingsBySubjectAndSemesterNumbers(@Param("subjectId") Long subjectId, @Param("semesterNumbers") List<Integer> semesterNumbers);
}

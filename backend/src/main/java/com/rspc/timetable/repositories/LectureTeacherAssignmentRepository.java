package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.LectureTeacherAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface LectureTeacherAssignmentRepository extends JpaRepository<LectureTeacherAssignment, Long> {
    Optional<LectureTeacherAssignment> findByDivision_IdAndSubject_Id(Long divisionId, Long subjectId);
    List<LectureTeacherAssignment> findByDivision_Id(Long divisionId);
}

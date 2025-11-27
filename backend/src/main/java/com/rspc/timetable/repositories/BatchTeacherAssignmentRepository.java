package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.BatchTeacherAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface BatchTeacherAssignmentRepository extends JpaRepository<BatchTeacherAssignment, Long> {
    Optional<BatchTeacherAssignment> findByBatch_IdAndSubject_Id(Long batchId, Long subjectId);
    List<BatchTeacherAssignment> findByBatch_Id(Long batchId);
}

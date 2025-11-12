package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    Optional<Subject> findByCode(String code);

    // Corrected: Subject has field 'semester', not 'subject'
    List<Subject> findBySemester_SemesterNumber(int semesterNumber);
}

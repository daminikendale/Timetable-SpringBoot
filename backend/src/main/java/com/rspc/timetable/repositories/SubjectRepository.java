package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    /**
     * NEW METHOD: Finds a subject by its unique code.
     * This is essential for the bulk update/create logic in the service.
     */
    Optional<Subject> findByCode(String code);
}


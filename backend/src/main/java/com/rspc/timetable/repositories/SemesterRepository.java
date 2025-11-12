package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {
     Optional<Semester> findBySemesterNumber(int semesterNumber);
}

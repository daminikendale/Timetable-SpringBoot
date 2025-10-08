package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.ElectiveGroup;
import com.rspc.timetable.entities.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ElectiveGroupRepository extends JpaRepository<ElectiveGroup, Long> {
    // Add this method
    Optional<ElectiveGroup> findBySemester(Semester semester);
}

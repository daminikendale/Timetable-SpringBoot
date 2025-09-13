package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.WorkingHours;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkingHoursRepository extends JpaRepository<WorkingHours, Long> {
}

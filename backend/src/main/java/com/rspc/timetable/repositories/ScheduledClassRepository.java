package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.ScheduledClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ScheduledClassRepository extends JpaRepository<ScheduledClass, Long> {

    // This method is correct
    List<ScheduledClass> findByDivisionIdOrderByDayOfWeekAscTimeSlotAsc(Long divisionId);

    // THIS IS THE CORRECTED METHOD NAME
    // It now correctly looks for the 'teacher' field on the ScheduledClass entity
    List<ScheduledClass> findByTeacherIdOrderByDayOfWeekAscTimeSlotAsc(Long teacherId);
}

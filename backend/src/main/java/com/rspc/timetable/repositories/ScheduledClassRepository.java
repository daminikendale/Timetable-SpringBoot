package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.ScheduledClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface ScheduledClassRepository extends JpaRepository<ScheduledClass, Long> {
    
    // For the substitution service
    List<ScheduledClass> findAllByTeacherIdAndDayOfWeekAndTimeSlotIdBetween(
        Long teacherId, 
        DayOfWeek dayOfWeek, 
        Long startSlotId, 
        Long endSlotId
    );

    // For the substitution service
    List<ScheduledClass> findByTeacherIdAndDayOfWeekAndTimeSlotId(
        Long teacherId, 
        DayOfWeek dayOfWeek, 
        Long timeSlotId
    );

    // For fetching a teacher's schedule, sorted
    List<ScheduledClass> findByTeacherIdOrderByDayOfWeekAscTimeSlotAsc(Long teacherId);


    List<ScheduledClass> findByDivisionId(Long divisionId);

    // ✅ ADD THIS NEW METHOD TO FIX THE CURRENT ERROR
    // For fetching a division's schedule, sorted
    List<ScheduledClass> findByDivisionIdOrderByDayOfWeekAscTimeSlotAsc(Long divisionId);
}

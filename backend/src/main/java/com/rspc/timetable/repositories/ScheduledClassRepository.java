package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.ScheduledClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduledClassRepository extends JpaRepository<ScheduledClass, Long> {

    List<ScheduledClass> findByDivision_Id(Long divisionId);

    List<ScheduledClass> findByDivision_IdOrderByDayOfWeekAscTimeSlot_StartTimeAsc(Long divisionId);

    List<ScheduledClass> findByTeacher_Id(Long teacherId);

    List<ScheduledClass> findByTeacher_IdOrderByDayOfWeekAscTimeSlot_StartTimeAsc(Long teacherId);

    // deletion by division
    void deleteByDivision_Id(Long divisionId);

    // delete all scheduled classes for a semester (JPQL update) - must be executed inside transaction
    @Modifying
    @Query("DELETE FROM ScheduledClass sc WHERE sc.courseOffering.semester.id = :semesterId")
    void deleteBySemesterId(@Param("semesterId") Long semesterId);

    // find teacher's scheduled classes on a given day/time (used for validation)
    List<ScheduledClass> findByTeacher_IdAndDayOfWeekAndTimeSlot_Id(Long teacherId, String dayOfWeek, Long timeSlotId);
}

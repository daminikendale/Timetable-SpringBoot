package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.ScheduledClass;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.util.List;

public interface ScheduledClassRepository extends JpaRepository<ScheduledClass, Long> {

    // Division timetable (sorted)
    List<ScheduledClass> findByDivision_IdOrderByDayOfWeekAscTimeSlot_StartTimeAsc(Long divisionId);

    // Teacher timetable (sorted)
    List<ScheduledClass> findByTeacher_IdOrderByDayOfWeekAscTimeSlot_StartTimeAsc(Long teacherId);

    // Basic division lookup
    List<ScheduledClass> findByDivision_Id(Long divisionId);

    // Basic teacher lookup
    List<ScheduledClass> findByTeacher_Id(Long teacherId);

    // Query to check teacher availability at a specific timeslot/day
    List<ScheduledClass> findByTeacher_IdAndDayOfWeekAndTimeSlot_Id(Long teacherId, DayOfWeek dayOfWeek, Long timeSlotId);

    // Range-style query: timeslot id between two values (if you index/number timeslots)
    List<ScheduledClass> findAllByTeacher_IdAndDayOfWeekAndTimeSlot_IdBetween(Long teacherId, DayOfWeek dayOfWeek, Long startTimeSlotId, Long endTimeSlotId);

    // Filter by semester via courseOffering -> semester
    List<ScheduledClass> findByCourseOffering_Semester_Id(Long semesterId);

    List<ScheduledClass> findByDivision_IdAndSubject_Semester_Id(Long divisionId, Long semesterId);

    boolean existsByTeacher_IdAndDayOfWeekAndTimeSlot_Id(Long teacherId, DayOfWeek dayOfWeek, Long timeSlotId);

    @Transactional
    @Modifying
    @Query("DELETE FROM ScheduledClass sc WHERE sc.division.id = :divisionId")
    void deleteByDivisionId(Long divisionId);

    // Convenience delete by division -> semester (deletes classes for divisions belonging to semester)
    @Transactional
    @Modifying
    @Query("DELETE FROM ScheduledClass sc WHERE sc.division.id IN (SELECT sd.division.id FROM SemesterDivision sd WHERE sd.semester.id = :semesterId)")
    void deleteByDivision_Semester_Id(Long semesterId);

    // If you prefer a simple Spring derived delete (works if you have mapping)
    @Transactional
    void deleteByDivision_Id(Long divisionId);

    @Modifying
    @Transactional
    @Query("""
        delete from ScheduledClass sc
        where sc.division.id in (
            select sd.division.id
            from SemesterDivision sd
            where sd.semester.id = :semesterId
        )
    """)
    void deleteBySemesterId(@Param("semesterId") Long semesterId);
}

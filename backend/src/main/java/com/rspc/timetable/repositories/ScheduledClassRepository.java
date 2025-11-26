package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.ScheduledClass;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.DayOfWeek;
import java.util.List;

public interface ScheduledClassRepository extends JpaRepository<ScheduledClass, Long> {

    List<ScheduledClass> findByDivision_IdOrderByDayOfWeekAscTimeSlot_StartTimeAsc(Long divisionId);

    List<ScheduledClass> findByTeacher_IdOrderByDayOfWeekAscTimeSlot_StartTimeAsc(Long teacherId);

    List<ScheduledClass> findByTeacher_Id(Long teacherId);

    List<ScheduledClass> findByTeacher_IdAndDayOfWeekAndTimeSlot_Id(
            Long teacherId, DayOfWeek dayOfWeek, Long timeSlotId);

    List<ScheduledClass> findByDivision_Id(Long divisionId);   // ✅ added

    List<ScheduledClass> findBySubject_Semester_Id(Long semesterId);

    List<ScheduledClass> findByDivision_IdAndSubject_Semester_Id(Long divisionId, Long semesterId);

    boolean existsByTeacher_IdAndDayOfWeekAndTimeSlot_Id(
            Long teacherId, DayOfWeek dayOfWeek, Long timeSlotId);

    List<ScheduledClass> findByCourseOffering_Semester_Id(Long semesterId);

    @Transactional
    void deleteByCourseOffering_Semester_Id(Long semesterId);

    @Transactional
    void deleteByDivision_Id(Long divisionId);

    @Transactional
@Modifying
@Query("DELETE FROM ScheduledClass sc WHERE sc.division.id = :divisionId")
void deleteByDivisionId(Long divisionId);

}

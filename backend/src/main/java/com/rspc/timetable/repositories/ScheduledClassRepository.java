package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.ScheduledClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduledClassRepository extends JpaRepository<ScheduledClass, Long> {

    // For timetable views
    List<ScheduledClass> findByDivision_Id(Long divisionId);

    List<ScheduledClass> findByDivision_IdOrderByDayOfWeekAscTimeSlot_StartTimeAsc(Long divisionId);

    List<ScheduledClass> findByTeacher_Id(Long teacherId);

    List<ScheduledClass> findByTeacher_IdOrderByDayOfWeekAscTimeSlot_StartTimeAsc(Long teacherId);

    // REQUIRED: Your code calls this → must exist
    void deleteByDivision_Id(Long divisionId);

    // REQUIRED: Your code calls this in saveSolution()
    @Modifying
    @Query("DELETE FROM ScheduledClass sc WHERE sc.courseOffering.semester.id = :semesterId")
    void deleteBySemesterId(@Param("semesterId") Long semesterId);

    List<ScheduledClass> findByTeacher_IdAndDayOfWeekAndTimeSlot_Id(
        Long teacherId,
        String dayOfWeek,
        Long timeSlotId
);

}

package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.ScheduledClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface ScheduledClassRepository extends JpaRepository<ScheduledClass, Long> {

    // 🔹 For substitution service
    List<ScheduledClass> findAllByTeacherIdAndDayOfWeekAndTimeSlotIdBetween(
            Long teacherId,
            DayOfWeek dayOfWeek,
            Long startSlotId,
            Long endSlotId
    );

    // 🔹 For specific slot conflict check
    List<ScheduledClass> findByTeacherIdAndDayOfWeekAndTimeSlotId(
            Long teacherId,
            DayOfWeek dayOfWeek,
            Long timeSlotId
    );

    // 🔹 For fetching a teacher’s full timetable (sorted)
    List<ScheduledClass> findByTeacherIdOrderByDayOfWeekAscTimeSlotAsc(Long teacherId);

    // 🔹 For fetching a division’s full timetable (unsorted)
    List<ScheduledClass> findByDivisionId(Long divisionId);

    // 🔹 For fetching a division’s full timetable (sorted)
    List<ScheduledClass> findByDivisionIdOrderByDayOfWeekAscTimeSlotAsc(Long divisionId);

    // 🔹 For deleting or loading timetable of multiple divisions at once
    List<ScheduledClass> findByDivisionIdIn(List<Long> divisionIds);
}

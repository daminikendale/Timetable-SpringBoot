// src/main/java/com/rspc/timetable/repositories/TimetableRepository.java
package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {

    // already present:
    List<Timetable> findByDivision_IdAndOverrideStartDateLessThanEqualAndOverrideEndDateGreaterThanEqual(
            Long divisionId, LocalDate date1, LocalDate date2);

    List<Timetable> findByDivision_IdAndOverrideStartDateIsNull(Long divisionId);

    List<Timetable> findByElectiveGroup(String group);

    // new helpers for targeting base rows
    List<Timetable> findByDivision_IdAndOverrideStartDateIsNullAndTimeSlot_IdIn(Long divisionId, List<Long> timeSlotIds);
    List<Timetable> findByDivision_IdAndOverrideStartDateIsNullAndSubject_Id(Long divisionId, Long subjectId);
    List<Timetable> findByDivision_IdAndOverrideStartDateIsNullAndTeacher_Id(Long divisionId, Long teacherId);

    // bulk remove base rows when regenerating permanent TT
    void deleteByDivision_IdAndOverrideStartDateIsNull(Long divisionId);
}

package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {
    List<Timetable> findByDivision_IdAndOverrideStartDateIsNull(Long divisionId);
    List<Timetable> findByDivision_IdAndOverrideStartDateLessThanEqualAndOverrideEndDateGreaterThanEqual(
            Long divisionId, LocalDate dateFrom, LocalDate dateTo
    );
    List<Timetable> findByElectiveGroup(String electiveGroup);
}

package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    /**
     * This is the method you need to add.
     * Spring Data JPA will automatically generate a query based on the method name,
     * finding all TimeSlot entities where the 'isBreak' field matches the
     * provided boolean value.
     */
    List<TimeSlot> findByIsBreak(boolean isBreak);

}

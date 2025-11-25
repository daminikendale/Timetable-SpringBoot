package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.TeacherUnavailableTimeslot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherUnavailableTimeslotRepository extends JpaRepository<TeacherUnavailableTimeslot, Long> {
    // default methods are enough for now
}

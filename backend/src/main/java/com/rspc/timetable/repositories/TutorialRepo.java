package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.Tutorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TutorialRepo extends JpaRepository<Tutorial, Long> {
}

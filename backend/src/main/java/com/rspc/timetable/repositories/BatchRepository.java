package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.Batch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BatchRepository extends JpaRepository<Batch, Long> {
    List<Batch> findByDivisionId(Long divisionId);
}

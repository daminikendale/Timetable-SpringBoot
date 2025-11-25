package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BatchRepository extends JpaRepository<Batch, Long> {

    // preferred: matches Batch.entity -> private Division division;
    List<Batch> findByDivision_Id(Long divisionId);

    // If you really prefer findByDivisionId(...) you could add an alias:
    // List<Batch> findByDivisionId(Long divisionId);
}

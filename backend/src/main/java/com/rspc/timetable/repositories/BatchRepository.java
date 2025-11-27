package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BatchRepository extends JpaRepository<Batch, Long> {
    // both versions so your code can call either findByDivisionId(...) or findByDivision_Id(...)
    List<Batch> findByDivisionId(Long divisionId);
    List<Batch> findByDivision_Id(Long divisionId);
}

package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.ElectiveGroupOption;
import com.rspc.timetable.entities.ElectiveGroupOptionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ElectiveGroupOptionRepository
        extends JpaRepository<ElectiveGroupOption, ElectiveGroupOptionId> {
    List<ElectiveGroupOption> findByElectiveGroup_Id(Long groupId);
}

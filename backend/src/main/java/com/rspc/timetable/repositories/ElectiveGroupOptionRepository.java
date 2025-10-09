package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.ElectiveGroupOption;
import com.rspc.timetable.entities.ElectiveGroupOptionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElectiveGroupOptionRepository extends JpaRepository<ElectiveGroupOption, ElectiveGroupOptionId> {
}

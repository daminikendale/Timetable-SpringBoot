package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.ElectiveAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElectiveAllocationRepository extends JpaRepository<ElectiveAllocation, Long> {
}

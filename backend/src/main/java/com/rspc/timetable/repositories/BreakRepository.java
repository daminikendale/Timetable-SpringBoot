// src/main/java/com/rspc/timetable/repositories/BreakRepository.java
package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.Break;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BreakRepository extends JpaRepository<Break, Long> {
}

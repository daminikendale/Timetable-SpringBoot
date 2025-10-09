package com.rspc.timetable.repositories;

import com.rspc.timetable.entities.Substitution;
import com.rspc.timetable.entities.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SubstitutionRepository extends JpaRepository<Substitution, Long> {

    // ✅ THIS IS THE CORRECTED METHOD NAME
    // Finds if a substitute is already covering a class by looking inside the scheduledClass for the timeSlot
    List<Substitution> findBySubstituteTeacherIdAndSubstitutionDateAndScheduledClass_TimeSlot(
        Long substituteTeacherId, 
        LocalDate substitutionDate, 
        TimeSlot timeSlot
    );
}

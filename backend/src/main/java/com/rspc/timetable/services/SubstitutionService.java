package com.rspc.timetable.services;

import com.rspc.timetable.entities.*;
import com.rspc.timetable.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubstitutionService {

    private final ScheduledClassRepository scheduledClassRepository;
    private final TeacherSubjectAllocationRepository teacherSubjectAllocationRepository;
    private final SubstitutionRepository substitutionRepository;

    public List<Substitution> assignSubstitutesForRange(Long absentTeacherId, LocalDate date, Long startTimeSlotId, Long endTimeSlotId) {
        List<ScheduledClass> classesToCover = scheduledClassRepository.findAllByTeacherIdAndDayOfWeekAndTimeSlotIdBetween(
                absentTeacherId, date.getDayOfWeek(), startTimeSlotId, endTimeSlotId);
        
        List<Substitution> createdSubstitutions = new ArrayList<>();

        for (ScheduledClass classToCover : classesToCover) {
            List<Teacher> qualifiedSubstitutes = teacherSubjectAllocationRepository.findAllBySubject(classToCover.getCourseOffering().getSubject())
                    .stream()
                    .map(TeacherSubjectAllocation::getTeacher)
                    .filter(teacher -> !teacher.getId().equals(absentTeacherId))
                    .collect(Collectors.toList());

            Teacher substituteFound = null;
            for (Teacher potentialSubstitute : qualifiedSubstitutes) {
                boolean isFreeInPermanentSchedule = scheduledClassRepository
                    .findByTeacherIdAndDayOfWeekAndTimeSlotId(potentialSubstitute.getId(), classToCover.getDayOfWeek(), classToCover.getTimeSlot().getId())
                    .isEmpty();
                
                // ✅ CORRECTED THIS LINE
                boolean isFreeInTemporarySchedule = substitutionRepository
                    .findBySubstituteTeacherIdAndSubstitutionDateAndScheduledClass_TimeSlot(
                        potentialSubstitute.getId(), 
                        date, 
                        classToCover.getTimeSlot()
                    ).isEmpty();

                if (isFreeInPermanentSchedule && isFreeInTemporarySchedule) {
                    substituteFound = potentialSubstitute;
                    break;
                }
            }

            if (substituteFound != null) {
                Substitution sub = new Substitution();
                sub.setScheduledClass(classToCover);
                sub.setOriginalTeacher(classToCover.getTeacher());
                sub.setSubstituteTeacher(substituteFound);
                sub.setSubstitutionDate(date);
                createdSubstitutions.add(substitutionRepository.save(sub));
            }
        }
        return createdSubstitutions;
    }
}

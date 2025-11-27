package com.rspc.timetable.services;

import com.rspc.timetable.entities.*;
import com.rspc.timetable.repositories.ScheduledClassRepository;
import com.rspc.timetable.repositories.SubstitutionRepository;
import com.rspc.timetable.repositories.TeacherSubjectAllocationRepository;
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

    public List<Substitution> assignSubstitutesForRange(
            Long absentTeacherId,
            LocalDate date,
            Long startTimeSlotId,
            Long endTimeSlotId
    ) {

        // fetch all classes by teacher
        List<ScheduledClass> dailyClasses =
                scheduledClassRepository.findByTeacher_Id(absentTeacherId);

        String dayString = date.getDayOfWeek().name(); // convert enum → String

        // filter only classes on same day + within requested timeslot range
        List<ScheduledClass> classesToCover = dailyClasses.stream()
                .filter(sc -> sc.getDayOfWeek().equals(dayString)) // FIXED
                .filter(sc -> {
                    Long tsId = sc.getTimeSlot().getId();
                    return tsId >= startTimeSlotId && tsId <= endTimeSlotId;
                })
                .collect(Collectors.toList());

        List<Substitution> created = new ArrayList<>();

        for (ScheduledClass classToCover : classesToCover) {

            // find qualified substitute teachers
            List<Teacher> qualifiedSubs =
                    teacherSubjectAllocationRepository
                            .findAllBySubject(classToCover.getSubject())
                            .stream()
                            .map(TeacherSubjectAllocation::getTeacher)
                            .filter(t -> !t.getId().equals(absentTeacherId))
                            .collect(Collectors.toList());

            Teacher chosen = null;

            for (Teacher t : qualifiedSubs) {

                boolean freePermanent =
                        scheduledClassRepository
                                .findByTeacher_IdAndDayOfWeekAndTimeSlot_Id(
                                        t.getId(),
                                        dayString, // FIXED
                                        classToCover.getTimeSlot().getId()
                                )
                                .isEmpty();

                boolean freeTemporary =
                        substitutionRepository
                                .findBySubstituteTeacherIdAndSubstitutionDateAndScheduledClass_TimeSlot(
                                        t.getId(),
                                        date,
                                        classToCover.getTimeSlot()
                                )
                                .isEmpty();

                if (freePermanent && freeTemporary) {
                    chosen = t;
                    break;
                }
            }

            if (chosen != null) {
                Substitution sub = new Substitution();
                sub.setScheduledClass(classToCover);
                sub.setOriginalTeacher(classToCover.getTeacher());
                sub.setSubstituteTeacher(chosen);
                sub.setSubstitutionDate(date);
                created.add(substitutionRepository.save(sub));
            }
        }

        return created;
    }
}

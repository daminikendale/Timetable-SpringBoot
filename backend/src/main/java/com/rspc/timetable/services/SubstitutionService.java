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

        // Fetch all classes for that teacher
        List<ScheduledClass> dailyClasses =
                scheduledClassRepository.findByTeacher_Id(absentTeacherId);

        // Filter classes only on same day + timeslot range
        List<ScheduledClass> classesToCover = dailyClasses.stream()
                .filter(sc -> sc.getDayOfWeek() == date.getDayOfWeek())
                .filter(sc -> {
                    Long tsId = sc.getTimeSlot().getId();
                    return tsId >= startTimeSlotId && tsId <= endTimeSlotId;
                })
                .collect(Collectors.toList());

        List<Substitution> created = new ArrayList<>();

        for (ScheduledClass classToCover : classesToCover) {

            // Teachers who can teach this subject
            List<Teacher> qualifiedSubs =
                    teacherSubjectAllocationRepository.findAllBySubject(classToCover.getSubject())
                            .stream()
                            .map(TeacherSubjectAllocation::getTeacher)
                            .filter(t -> !t.getId().equals(absentTeacherId))   // remove absent teacher
                            .collect(Collectors.toList());

            Teacher chosen = null;

            for (Teacher t : qualifiedSubs) {

                boolean freePermanent =
                        scheduledClassRepository.findByTeacher_IdAndDayOfWeekAndTimeSlot_Id(
                                t.getId(),
                                classToCover.getDayOfWeek(),
                                classToCover.getTimeSlot().getId()
                        ).isEmpty();

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

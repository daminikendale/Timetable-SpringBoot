package com.rspc.timetable.services;

import com.rspc.timetable.entities.*;
import com.rspc.timetable.repositories.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimetableGeneratorService {

    private static final Logger logger = LoggerFactory.getLogger(TimetableGeneratorService.class);

    private final ScheduledClassRepository scheduledClassRepository;
    private final CourseOfferingRepository courseOfferingRepository;
    private final ClassroomRepository classroomRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final SemesterDivisionRepository semesterDivisionRepository;
    private final TeacherSubjectAllocationRepository teacherSubjectAllocationRepository;

    public enum SemesterType { ODD, EVEN }
    private enum SessionType { LECTURE, LAB, TUTORIAL }

    @Transactional
    public String generateTimetableFor(SemesterType semesterType) {
        try {
            // 1. Setup
            List<Integer> targetSemesterNumbers = semesterType == SemesterType.ODD
                ? List.of(1, 3, 5, 7)
                : List.of(2, 4, 6, 8);
            logger.info("Starting timetable generation for {} semesters: {}", semesterType, targetSemesterNumbers);

            scheduledClassRepository.deleteAllInBatch();
            logger.info("Cleared all existing scheduled classes.");

            // ✅ FIX: Calling the new, explicitly defined repository method
            List<CourseOffering> relevantCourseOfferings = courseOfferingRepository.findOfferingsBySemesterNumbers(targetSemesterNumbers);
            
            List<Classroom> allClassrooms = classroomRepository.findAll();
            List<TimeSlot> allTimeSlots = timeSlotRepository.findByIsBreak(false);
            List<DayOfWeek> weekDays = List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);

            Map<Long, List<Teacher>> subjectToTeachersMap = teacherSubjectAllocationRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                    alloc -> alloc.getSubject().getId(),
                    Collectors.mapping(TeacherSubjectAllocation::getTeacher, Collectors.toList())
                ));

            Map<Long, List<Division>> semesterToDivisionsMap = semesterDivisionRepository.findBySemester_SemesterNumberIn(targetSemesterNumbers)
                .stream()
                .collect(Collectors.groupingBy(
                    sd -> sd.getSemester().getId(),
                    Collectors.mapping(SemesterDivision::getDivision, Collectors.toList())
                ));

            List<ScheduledClass> generatedSchedule = new ArrayList<>();

            // 2. Core Scheduling Logic
            for (CourseOffering offering : relevantCourseOfferings) {
                Long semesterId = offering.getSubject().getSemester().getId();
                List<Division> applicableDivisions = semesterToDivisionsMap.getOrDefault(semesterId, Collections.emptyList());
                if (applicableDivisions.isEmpty()) continue;

                List<Teacher> availableTeachers = subjectToTeachersMap.getOrDefault(offering.getSubject().getId(), Collections.emptyList());
                if (availableTeachers.isEmpty()) {
                    logger.warn("No teachers allocated for subject: {}. Skipping.", offering.getSubject().getName());
                    continue;
                }

                Collections.shuffle(applicableDivisions);
                Division targetDivision = applicableDivisions.get(0);

                Collections.shuffle(availableTeachers);
                Teacher assignedTeacher = availableTeachers.get(0);

                scheduleSessionType(offering, assignedTeacher, targetDivision, SessionType.LECTURE, offering.getLecPerWeek(), 1, generatedSchedule, allClassrooms, allTimeSlots, weekDays);
                scheduleSessionType(offering, assignedTeacher, targetDivision, SessionType.LAB, offering.getLabPerWeek(), 2, generatedSchedule, allClassrooms, allTimeSlots, weekDays);
                scheduleSessionType(offering, assignedTeacher, targetDivision, SessionType.TUTORIAL, offering.getTutPerWeek(), 1, generatedSchedule, allClassrooms, allTimeSlots, weekDays);
            }

            // 3. Finalization
            scheduledClassRepository.saveAll(generatedSchedule);
            logger.info("Successfully generated and saved {} new scheduled classes.", generatedSchedule.size());
            return "Timetable for " + semesterType + " semesters generated successfully with " + generatedSchedule.size() + " entries.";
        } catch (Exception e) {
            logger.error("Error during timetable generation", e);
            throw new RuntimeException("Error generating timetable: " + e.getMessage(), e);
        }
    }

    private void scheduleSessionType(CourseOffering offering, Teacher teacher, Division division, SessionType sessionType, int hoursToSchedule, int blockLength, List<ScheduledClass> generatedSchedule, List<Classroom> allClassrooms, List<TimeSlot> allTimeSlots, List<DayOfWeek> weekDays) {
        if (hoursToSchedule <= 0) return;

        int numBlocksToSchedule = hoursToSchedule / blockLength;

        for (int i = 0; i < numBlocksToSchedule; i++) {
            boolean slotFound = false;
            Collections.shuffle(weekDays);
            for (DayOfWeek day : weekDays) {
                Collections.shuffle(allTimeSlots);
                for (int j = 0; j <= allTimeSlots.size() - blockLength; j++) {
                    List<TimeSlot> potentialBlock = allTimeSlots.subList(j, j + blockLength);
                    Classroom suitableClassroom = findSuitableClassroom(allClassrooms, sessionType);

                    if (suitableClassroom != null && isBlockAvailable(teacher, division, suitableClassroom, day, potentialBlock, generatedSchedule)) {
                        for (TimeSlot slot : potentialBlock) {
                            ScheduledClass newClass = new ScheduledClass();
                            newClass.setCourseOffering(offering);
                            newClass.setTeacher(teacher);
                            newClass.setDivision(division);
                            newClass.setClassroom(suitableClassroom);
                            newClass.setTimeSlot(slot);
                            newClass.setDayOfWeek(day);
                            newClass.setSessionType(ScheduledClass.SessionType.valueOf(sessionType.name()));
                            generatedSchedule.add(newClass);
                        }
                        slotFound = true;
                        break;
                    }
                }
                if (slotFound) break;
            }
            if (!slotFound) {
                logger.warn("Could not schedule {} block for subject: {} for division: {}", sessionType, offering.getSubject().getName(), division.getDivisionName());
            }
        }
    }

    private boolean isBlockAvailable(Teacher teacher, Division division, Classroom classroom, DayOfWeek day, List<TimeSlot> block, List<ScheduledClass> schedule) {
        for (TimeSlot slot : block) {
            if (!isTeacherAvailable(teacher, day, slot, schedule) ||
                !isClassroomAvailable(classroom, day, slot, schedule) ||
                !isDivisionAvailable(division, day, slot, schedule)) {
                return false;
            }
        }
        return true;
    }

    private Classroom findSuitableClassroom(List<Classroom> allClassrooms, SessionType sessionType) {
        Collections.shuffle(allClassrooms);
        if (sessionType == SessionType.LAB) {
            return allClassrooms.stream().filter(r -> r.getType() == Classroom.ClassroomType.LAB).findFirst().orElse(null);
        }
        if (sessionType == SessionType.TUTORIAL) {
            return allClassrooms.stream().filter(r -> r.getType() == Classroom.ClassroomType.TUTORIAL_ROOM).findFirst().orElse(null);
        }
        return allClassrooms.stream().filter(r -> r.getType() == Classroom.ClassroomType.LECTURE_HALL).findFirst().orElse(null);
    }

    private boolean isTeacherAvailable(Teacher teacher, DayOfWeek day, TimeSlot slot, List<ScheduledClass> currentSchedule) {
        return currentSchedule.stream().noneMatch(sc ->
            sc.getTeacher().getId().equals(teacher.getId()) &&
            sc.getDayOfWeek() == day &&
            sc.getTimeSlot().getId().equals(slot.getId())
        );
    }

    private boolean isClassroomAvailable(Classroom classroom, DayOfWeek day, TimeSlot slot, List<ScheduledClass> currentSchedule) {
        return currentSchedule.stream().noneMatch(sc ->
            sc.getClassroom().getId().equals(classroom.getId()) &&
            sc.getDayOfWeek() == day &&
            sc.getTimeSlot().getId().equals(slot.getId())
        );
    }

    private boolean isDivisionAvailable(Division division, DayOfWeek day, TimeSlot slot, List<ScheduledClass> currentSchedule) {
        return currentSchedule.stream().noneMatch(sc ->
            sc.getDivision().getId().equals(division.getId()) &&
            sc.getDayOfWeek() == day &&
            sc.getTimeSlot().getId().equals(slot.getId())
        );
    }
}

package com.rspc.timetable.services;

import com.rspc.timetable.entities.*;
import com.rspc.timetable.repositories.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimetableGeneratorService {

    private static final Logger logger = LoggerFactory.getLogger(TimetableGeneratorService.class);

    // (Repositories remain the same)
    private final ScheduledClassRepository scheduledClassRepository;
    private final CourseOfferingRepository courseOfferingRepository;
    private final ClassroomRepository classroomRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final SemesterDivisionRepository semesterDivisionRepository;
    private final TeacherSubjectAllocationRepository teacherSubjectAllocationRepository;
    private final ElectiveGroupOptionRepository electiveGroupOptionRepository;

    public enum SemesterType { ODD, EVEN }
    private enum SessionType { LECTURE, LAB, TUTORIAL }

    @Transactional
    public String generateTimetableFor(SemesterType semesterType) {
        try {
            // 1. SETUP PHASE
            List<Integer> targetSemesterNumbers = semesterType == SemesterType.ODD ? List.of(1, 3, 5, 7) : List.of(2, 4, 6, 8);
            logger.info("Starting timetable generation for {} semesters: {}", semesterType, targetSemesterNumbers);

            scheduledClassRepository.deleteAllInBatch();
            logger.info("Cleared all existing scheduled classes.");
            
            List<CourseOffering> allCourseOfferings = courseOfferingRepository.findBySubject_Semester_SemesterNumberIn(targetSemesterNumbers)
                .stream().filter(co -> co.getSubject() != null && co.getSubject().getSemester() != null)
                .collect(Collectors.toList());

            List<Classroom> allClassrooms = classroomRepository.findAll();
            List<TimeSlot> allTimeSlots = timeSlotRepository.findByIsBreak(false);
            allTimeSlots.sort(Comparator.comparing(TimeSlot::getStartTime));
            List<DayOfWeek> weekDays = List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);

            Map<Long, List<Teacher>> subjectToTeachersMap = teacherSubjectAllocationRepository.findAll().stream()
                .filter(alloc -> alloc.getSubject() != null && alloc.getTeacher() != null)
                .collect(Collectors.groupingBy(
                    alloc -> alloc.getSubject().getId(),
                    Collectors.mapping(TeacherSubjectAllocation::getTeacher, Collectors.toList())
                ));

            // ✅✅✅ THE FIX: Load ALL semester-to-division mappings ✅✅✅
            // This ensures that when a course for any semester is processed, its corresponding divisions are available.
            Map<Long, List<Division>> semesterToDivisionsMap = semesterDivisionRepository.findAll().stream()
                .filter(sd -> sd.getSemester() != null && sd.getDivision() != null)
                .collect(Collectors.groupingBy(
                    sd -> sd.getSemester().getId(),
                    Collectors.mapping(SemesterDivision::getDivision, Collectors.toList())
                ));
            logger.info("Loaded {} semester-to-division mappings.", semesterToDivisionsMap.size());


            // 2. PRE-PROCESSING FOR ELECTIVES (logic is sound)
            // ... (rest of the method remains the same)
            
            List<ElectiveGroupOption> allElectiveOptions = electiveGroupOptionRepository.findAll();
            Set<Long> allElectiveSubjectIds = allElectiveOptions.stream()
                .map(opt -> opt.getSubject().getId())
                .collect(Collectors.toSet());

            Map<Long, List<CourseOffering>> electivesByGroup = allCourseOfferings.stream()
                .filter(co -> allElectiveSubjectIds.contains(co.getSubject().getId()))
                .collect(Collectors.groupingBy(co -> getElectiveGroupIdForSubject(co.getSubject(), allElectiveOptions)));
            
            List<CourseOffering> regularCourses = allCourseOfferings.stream()
                .filter(co -> !allElectiveSubjectIds.contains(co.getSubject().getId()))
                .collect(Collectors.toList());

            List<ScheduledClass> generatedSchedule = new ArrayList<>();

            // 3. CORE SCHEDULING LOGIC
            logger.info("Scheduling {} elective groups...", electivesByGroup.size());
            for (List<CourseOffering> electiveGroup : electivesByGroup.values()) {
                scheduleElectiveGroup(electiveGroup, generatedSchedule, allClassrooms, allTimeSlots, weekDays, semesterToDivisionsMap, subjectToTeachersMap);
            }

            logger.info("Scheduling {} regular courses...", regularCourses.size());
            for (CourseOffering offering : regularCourses) {
                scheduleIndividualCourse(offering, generatedSchedule, allClassrooms, allTimeSlots, weekDays, semesterToDivisionsMap, subjectToTeachersMap);
            }

            // 4. FINALIZATION
            scheduledClassRepository.saveAll(generatedSchedule);
            logger.info("Successfully generated and saved {} new scheduled classes.", generatedSchedule.size());
            return "Timetable for " + semesterType + " semesters generated successfully with " + generatedSchedule.size() + " entries.";

        } catch (Exception e) {
            logger.error("Fatal error during timetable generation", e);
            throw new RuntimeException("Error generating timetable: " + (e.getMessage() != null ? e.getMessage() : "A NullPointerException occurred."), e);
        }
    }

    // ... (The rest of the helper methods are correct and remain unchanged)

    private Long getElectiveGroupIdForSubject(Subject subject, List<ElectiveGroupOption> allOptions) {
        return allOptions.stream()
            .filter(opt -> opt.getSubject().getId().equals(subject.getId()))
            .map(opt -> opt.getElectiveGroup().getId())
            .findFirst()
            .orElse(-1L);
    }
    
    private void scheduleIndividualCourse(CourseOffering offering, List<ScheduledClass> generatedSchedule, List<Classroom> allClassrooms, List<TimeSlot> allTimeSlots, List<DayOfWeek> weekDays, Map<Long, List<Division>> semesterToDivisionsMap, Map<Long, List<Teacher>> subjectToTeachersMap){
        Subject subject = offering.getSubject();
        List<Division> applicableDivisions = semesterToDivisionsMap.getOrDefault(subject.getSemester().getId(), Collections.emptyList());
        List<Teacher> availableTeachers = subjectToTeachersMap.getOrDefault(subject.getId(), Collections.emptyList());

        if (applicableDivisions.isEmpty()) {
            logger.warn("Skipping subject '{}': No divisions assigned to its semester.", subject.getName());
            return;
        }
        if (availableTeachers.isEmpty()) {
            logger.warn("Skipping subject '{}': No teachers allocated.", subject.getName());
            return;
        }

        for (Division targetDivision : applicableDivisions) {
            Teacher assignedTeacher = availableTeachers.get(0);
            scheduleSessionType(offering, assignedTeacher, targetDivision, SessionType.LECTURE, offering.getLecPerWeek(), 1, generatedSchedule, allClassrooms, allTimeSlots, weekDays);
            scheduleSessionType(offering, assignedTeacher, targetDivision, SessionType.LAB, offering.getLabPerWeek(), 2, generatedSchedule, allClassrooms, allTimeSlots, weekDays);
            scheduleSessionType(offering, assignedTeacher, targetDivision, SessionType.TUTORIAL, offering.getTutPerWeek(), 1, generatedSchedule, allClassrooms, allTimeSlots, weekDays);
        }
    }
    
    private void scheduleSessionType(CourseOffering offering, Teacher teacher, Division division, SessionType sessionType, int hoursToSchedule, int blockLength, List<ScheduledClass> generatedSchedule, List<Classroom> allClassrooms, List<TimeSlot> allTimeSlots, List<DayOfWeek> weekDays) {
        if (hoursToSchedule <= 0) return;
        int numBlocksToSchedule = hoursToSchedule / blockLength;
        for (int i = 0; i < numBlocksToSchedule; i++) {
            boolean slotFound = false;
            List<DayOfWeek> shuffledDays = new ArrayList<>(weekDays);
            Collections.shuffle(shuffledDays);
            
            for (DayOfWeek day : shuffledDays) {
                List<TimeSlot> shuffledSlots = new ArrayList<>(allTimeSlots);
                Collections.shuffle(shuffledSlots);

                for (int j = 0; j <= shuffledSlots.size() - blockLength; j++) {
                    List<TimeSlot> potentialBlock = shuffledSlots.subList(j, j + blockLength);
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
                logger.warn("Could not schedule {} block for subject: {}", sessionType, offering.getSubject().getName());
            }
        }
    }
    
    private void scheduleElectiveGroup(List<CourseOffering> electiveGroup, List<ScheduledClass> generatedSchedule, List<Classroom> allClassrooms, List<TimeSlot> allTimeSlots, List<DayOfWeek> weekDays, Map<Long, List<Division>> semesterToDivisionsMap, Map<Long, List<Teacher>> subjectToTeachersMap) {
        if (electiveGroup.isEmpty()) return;

        CourseOffering representativeOffering = electiveGroup.get(0);
        int hoursToSchedule = representativeOffering.getLecPerWeek();
        SessionType sessionType = SessionType.LECTURE;
        int blockLength = 1;
        
        if (hoursToSchedule <= 0) return;

        boolean slotFound = false;
        List<DayOfWeek> shuffledDays = new ArrayList<>(weekDays);
        Collections.shuffle(shuffledDays);
        for (DayOfWeek day : shuffledDays) {
            List<TimeSlot> shuffledSlots = new ArrayList<>(allTimeSlots);
            Collections.shuffle(shuffledSlots);

            for (int j = 0; j <= shuffledSlots.size() - blockLength; j++) {
                List<TimeSlot> potentialBlock = shuffledSlots.subList(j, j + blockLength);
                boolean isGroupAvailable = true;

                for (CourseOffering offering : electiveGroup) {
                    List<Teacher> teachers = subjectToTeachersMap.getOrDefault(offering.getSubject().getId(), Collections.emptyList());
                    List<Division> divisions = semesterToDivisionsMap.getOrDefault(offering.getSubject().getSemester().getId(), Collections.emptyList());
                    
                    if (teachers.isEmpty() || divisions.isEmpty()) {
                        isGroupAvailable = false;
                        break;
                    }
                    if (!isBlockAvailable(teachers.get(0), divisions.get(0), null, day, potentialBlock, generatedSchedule)) {
                        isGroupAvailable = false;
                        break;
                    }
                }

                if (isGroupAvailable) {
                    for (CourseOffering offering : electiveGroup) {
                        Teacher teacher = subjectToTeachersMap.get(offering.getSubject().getId()).get(0);
                        Division division = semesterToDivisionsMap.get(offering.getSubject().getSemester().getId()).get(0);
                        Classroom classroom = findSuitableClassroom(allClassrooms, sessionType);

                        if (classroom != null && isClassroomAvailable(classroom, day, potentialBlock.get(0), generatedSchedule)) {
                            for (TimeSlot slot : potentialBlock) {
                                ScheduledClass newClass = new ScheduledClass();
                                newClass.setCourseOffering(offering);
                                newClass.setTeacher(teacher);
                                newClass.setDivision(division);
                                newClass.setClassroom(classroom);
                                newClass.setTimeSlot(slot);
                                newClass.setDayOfWeek(day);
                                newClass.setSessionType(ScheduledClass.SessionType.valueOf(sessionType.name()));
                                generatedSchedule.add(newClass);
                            }
                        } else {
                            logger.warn("No suitable classroom found for elective subject: {}", offering.getSubject().getName());
                        }
                    }
                    slotFound = true;
                    break;
                }
            }
            if (slotFound) break;
        }
        if (!slotFound) {
            logger.warn("Could not find a common slot for elective group containing: {}", representativeOffering.getSubject().getName());
        }
    }
    
    private boolean isBlockAvailable(Teacher teacher, Division division, Classroom classroom, DayOfWeek day, List<TimeSlot> block, List<ScheduledClass> schedule) {
        for (TimeSlot slot : block) {
            if (teacher != null && !isTeacherAvailable(teacher, day, slot, schedule)) return false;
            if (division != null && !isDivisionAvailable(division, day, slot, schedule)) return false;
            if (classroom != null && !isClassroomAvailable(classroom, day, slot, schedule)) return false;
        }
        
        if (teacher != null && createsExcessiveContinuousWork(teacher, day, block, schedule)) return false;

        return true;
    }

    private boolean createsExcessiveContinuousWork(Teacher teacher, DayOfWeek day, List<TimeSlot> newBlock, List<ScheduledClass> schedule) {
        final int MAX_CONTINUOUS_HOURS = 3;
        List<TimeSlot> proposedSlots = schedule.stream()
            .filter(sc -> sc.getTeacher().getId().equals(teacher.getId()) && sc.getDayOfWeek() == day)
            .map(ScheduledClass::getTimeSlot)
            .collect(Collectors.toList());
        proposedSlots.addAll(newBlock);
        proposedSlots.sort(Comparator.comparing(TimeSlot::getStartTime));

        if (proposedSlots.size() <= MAX_CONTINUOUS_HOURS) return false;

        int continuousCount = 1;
        for (int i = 0; i < proposedSlots.size() - 1; i++) {
            TimeSlot current = proposedSlots.get(i);
            TimeSlot next = proposedSlots.get(i + 1);

            if (current.getEndTime().equals(next.getStartTime())) {
                continuousCount++;
                if (continuousCount > MAX_CONTINUOUS_HOURS) {
                    logger.debug("Constraint violation: Teacher {} would have >3 continuous hours on {}", teacher.getName(), day);
                    return true;
                }
            } else {
                continuousCount = 1;
            }
        }
        return false;
    }

    private Classroom findSuitableClassroom(List<Classroom> allClassrooms, SessionType sessionType) {
        List<Classroom> suitableRooms = new ArrayList<>();
        if (sessionType == SessionType.LAB) {
            suitableRooms = allClassrooms.stream().filter(r -> r.getType() == Classroom.ClassroomType.LAB).collect(Collectors.toList());
        } else if (sessionType == SessionType.TUTORIAL) {
            suitableRooms = allClassrooms.stream().filter(r -> r.getType() == Classroom.ClassroomType.TUTORIAL_ROOM).collect(Collectors.toList());
        } else {
            suitableRooms = allClassrooms.stream().filter(r -> r.getType() == Classroom.ClassroomType.LECTURE_HALL).collect(Collectors.toList());
        }
        if(suitableRooms.isEmpty()) return null;
        Collections.shuffle(suitableRooms);
        return suitableRooms.get(0);
    }

    private boolean isTeacherAvailable(Teacher teacher, DayOfWeek day, TimeSlot slot, List<ScheduledClass> currentSchedule) {
        return currentSchedule.stream().noneMatch(sc -> sc.getTeacher().getId().equals(teacher.getId()) && sc.getDayOfWeek() == day && sc.getTimeSlot().getId().equals(slot.getId()));
    }

    private boolean isClassroomAvailable(Classroom classroom, DayOfWeek day, TimeSlot slot, List<ScheduledClass> currentSchedule) {
        return currentSchedule.stream().noneMatch(sc -> sc.getClassroom().getId().equals(classroom.getId()) && sc.getDayOfWeek() == day && sc.getTimeSlot().getId().equals(slot.getId()));
    }

    private boolean isDivisionAvailable(Division division, DayOfWeek day, TimeSlot slot, List<ScheduledClass> currentSchedule) {
        return currentSchedule.stream().noneMatch(sc -> sc.getDivision().getId().equals(division.getId()) && sc.getDayOfWeek() == day && sc.getTimeSlot().getId().equals(slot.getId()));
    }

}

package com.rspc.timetable.optaplanner;

import com.rspc.timetable.entities.*;
import com.rspc.timetable.repositories.ClassroomRepository;
import com.rspc.timetable.repositories.ScheduledClassRepository;
import com.rspc.timetable.repositories.TimeSlotRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TimetableProblemLoader {

    private final TimeSlotRepository timeSlotRepository;
    private final ClassroomRepository classroomRepository;
    private final ScheduledClassRepository scheduledClassRepository;

    public TimetableProblemLoader(TimeSlotRepository timeSlotRepository,
                                  ClassroomRepository classroomRepository,
                                  ScheduledClassRepository scheduledClassRepository) {
        this.timeSlotRepository = timeSlotRepository;
        this.classroomRepository = classroomRepository;
        this.scheduledClassRepository = scheduledClassRepository;
    }

    /**
     * Build TimetableSolution for a given semesterId.
     */
    public TimetableSolution loadProblemForSemester(Long semesterId) {
        List<TimeSlot> timeSlots = timeSlotRepository.findAll();
        List<Classroom> rooms = classroomRepository.findAll();

        // load scheduled classes via subject.semester.id
        List<ScheduledClass> scheduledClasses = scheduledClassRepository.findBySubject_Semester_Id(semesterId);

        // convert to PlannedClass list
        List<PlannedClass> plannedClasses = scheduledClasses.stream()
                .map(PlannedClass::new)
                .collect(Collectors.toList());

        return new TimetableSolution(timeSlots, rooms, plannedClasses);
    }
}

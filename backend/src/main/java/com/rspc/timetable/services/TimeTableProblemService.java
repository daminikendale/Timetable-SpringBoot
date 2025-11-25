package com.rspc.timetable.services;

import com.rspc.timetable.entities.Classroom;
import com.rspc.timetable.entities.ScheduledClass;
import com.rspc.timetable.entities.TimeSlot;
import com.rspc.timetable.optaplanner.PlannedClass;
import com.rspc.timetable.optaplanner.TimetableSolution;
import com.rspc.timetable.repositories.ClassroomRepository;
import com.rspc.timetable.repositories.ScheduledClassRepository;
import com.rspc.timetable.repositories.TimeSlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TimeTableProblemService {

    private final ClassroomRepository classroomRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final ScheduledClassRepository scheduledClassRepository;

    public TimeTableProblemService(
            ClassroomRepository classroomRepository,
            TimeSlotRepository timeSlotRepository,
            ScheduledClassRepository scheduledClassRepository) {
        this.classroomRepository = classroomRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.scheduledClassRepository = scheduledClassRepository;
    }

    @Transactional(readOnly = true)
    public TimetableSolution loadProblem(Long semId) {
        List<Classroom> rooms = classroomRepository.findAll();
        List<TimeSlot> slots = timeSlotRepository.findAll();

        List<PlannedClass> planned = scheduledClassRepository.findAll()
                .stream()
                .map(PlannedClass::new)
                .collect(Collectors.toList());

        return new TimetableSolution(slots, rooms, planned);
    }

    @Transactional
    public void saveSolution(TimetableSolution solution) {
        if (solution == null || solution.getPlannedClassList() == null) return;

        for (PlannedClass pc : solution.getPlannedClassList()) {
            if (pc == null) continue;
            ScheduledClass sc = pc.getScheduledClass();
            if (sc == null) continue;

            sc.setClassroom(pc.getRoom());
            sc.setTimeSlot(pc.getTimeSlot());
            scheduledClassRepository.save(sc);
        }
    }
}

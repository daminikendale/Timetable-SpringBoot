package com.rspc.timetable.services;

import com.rspc.timetable.entities.Classroom;
import com.rspc.timetable.entities.ScheduledClass;
import com.rspc.timetable.entities.TimeSlot;
import com.rspc.timetable.optaplanner.PlannedClass;
import com.rspc.timetable.optaplanner.TimetableSolution;
import com.rspc.timetable.repositories.ClassroomRepository;
import com.rspc.timetable.repositories.ScheduledClassRepository;
import com.rspc.timetable.repositories.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeTableProblemService {

    private final ClassroomRepository classroomRepo;
    private final TimeSlotRepository timeRepo;
    private final ScheduledClassRepository scheduledRepo;

    // -----------------------------
    // LOAD PROBLEM FOR OPTAPLANNER
    // -----------------------------
    public TimetableSolution load(Long semesterId) {

        List<Classroom> rooms = classroomRepo.findAll();
        List<TimeSlot> slots = timeRepo.findAll();

        List<ScheduledClass> scheduled =
                scheduledRepo.findByCourseOffering_Semester_Id(semesterId);

        List<PlannedClass> planned = scheduled.stream()
                .map(this::toPlannedClass)
                .collect(Collectors.toList());

        TimetableSolution solution = new TimetableSolution();
        solution.setTimeSlotList(slots);
        solution.setRoomList(rooms);
        solution.setPlannedClassList(planned);

        solution.setTeacherList(Collections.emptyList());
        solution.setDivisionList(Collections.emptyList());
        solution.setBatchList(Collections.emptyList());
        solution.setOfferingList(Collections.emptyList());

        return solution;
    }


    // -----------------------------
    // SAVE OPTIMIZED SOLUTION
    // -----------------------------
    public void saveSolution(TimetableSolution solution, Long semesterId) {

        if (solution == null || solution.getPlannedClassList() == null) return;

        for (PlannedClass pc : solution.getPlannedClassList()) {

            Long id = pc.getId();
            if (id == null) continue;

            Optional<ScheduledClass> optional = scheduledRepo.findById(id);

            optional.ifPresent(sc -> {

                sc.setClassroom(pc.getRoom());
                sc.setTimeSlot(pc.getTimeSlot());
                sc.setTeacher(pc.getTeacher());

                scheduledRepo.save(sc);
            });
        }
    }


    // -----------------------------
    // HELPERS
    // -----------------------------
    private PlannedClass toPlannedClass(ScheduledClass sc) {

        return new PlannedClass(
                sc.getId(),
                sc.getCourseOffering(),
                sc.getSubject(),
                sc.getDivision(),
                sc.getBatch(),
                sc.getSessionType(),
                false,   // multi-slot? No
                1        // duration = 1
        );
    }
}

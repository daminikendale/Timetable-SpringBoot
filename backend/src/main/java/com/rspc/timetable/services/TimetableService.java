package com.rspc.timetable.services;

import com.rspc.timetable.entities.*;
import com.rspc.timetable.repositories.TimetableRepository;
import org.springframework.stereotype.Service;
import com.rspc.timetable.dto.ElectiveDTO;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TimetableService {

    private final TimetableRepository timetableRepository;
    private final TimeSlotService timeSlotService;

    public TimetableService(TimetableRepository timetableRepository, TimeSlotService timeSlotService) {
        this.timetableRepository = timetableRepository;
        this.timeSlotService = timeSlotService;
    }

    // --- CRUD ---
    public Timetable saveTimetable(Timetable timetable) {
        return timetableRepository.save(timetable);
    }

    public List<Timetable> saveBulk(List<Timetable> timetables) {
        return timetableRepository.saveAll(timetables);
    }

    public List<Timetable> getAllTimetables() {
        return timetableRepository.findAll();
    }

    public Timetable getById(Long id) {
        return timetableRepository.findById(id).orElse(null);
    }

    public void delete(Long id) {
        timetableRepository.deleteById(id);
    }

    // --- Get timetable for division/date with overrides ---
    public List<Timetable> getTimetableForDivisionOnDate(Long divisionId, LocalDate date) {
        List<Timetable> override = timetableRepository
                .findByDivision_IdAndOverrideStartDateLessThanEqualAndOverrideEndDateGreaterThanEqual(
                        divisionId, date, date
                );
        if (!override.isEmpty()) return override;
        return timetableRepository.findByDivision_IdAndOverrideStartDateIsNull(divisionId);
    }

    // --- Electives ---
    public List<Timetable> getElectivesByGroup(String group) {
        return timetableRepository.findByElectiveGroup(group);
    }

    // --- Group electives by TimeSlot ---
    public List<ElectiveDTO> groupElectivesByTimeSlot(List<Timetable> electives) {
        Map<String, List<Timetable>> grouped = electives.stream()
                .collect(Collectors.groupingBy(e ->
                        e.getTimeSlot().getStartTime() + "-" + e.getTimeSlot().getEndTime()
                ));

        List<ElectiveDTO> result = new ArrayList<>();
        for (Map.Entry<String, List<Timetable>> entry : grouped.entrySet()) {
            String timeSlotStr = entry.getKey();
            List<Timetable> slotElectives = entry.getValue();

            List<String> subjects = slotElectives.stream()
                    .map(e -> e.getSubject().getName())
                    .collect(Collectors.toList());

            List<String> teachers = slotElectives.stream()
                    .map(e -> e.getTeacher().getName())
                    .collect(Collectors.toList());

            String classroom = slotElectives.get(0).getClassroom().getName();

            result.add(new ElectiveDTO(timeSlotStr, classroom, subjects, teachers));
        }

        result.sort(Comparator.comparing(ElectiveDTO::getTimeSlot));
        return result;
    }

    // --- Allocate subject hours ---
    public void allocateSubjectHours(Division division, Subject subject, Teacher teacher, Classroom classroom) {
        int hours = subject.getCredits();
        List<TimeSlot> availableSlots = timeSlotService.getAllTimeSlots();

        boolean isElective = subject.getCategory() != SubjectCategory.REGULAR;

        for (int i = 0; i < hours; i++) {
            TimeSlot slot = availableSlots.get(i);
            Timetable tt = new Timetable(
                    division,
                    subject,
                    teacher,
                    classroom,
                    slot,
                    false,           // override flag
                    isElective,      // elective flag
                    null, null, null // electiveGroup, overrideStartDate, overrideEndDate
            );
            timetableRepository.save(tt);
        }
    }
}

package com.rspc.timetable.services;

import com.rspc.timetable.dto.ScheduledClassDTO;
import com.rspc.timetable.entities.ScheduledClass;
import com.rspc.timetable.repositories.ScheduledClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduledClassService {

    private final ScheduledClassRepository scheduledClassRepository;

    @Transactional(readOnly = true)
    public List<ScheduledClassDTO> getTimetableForDivision(Long divisionId) {
        return scheduledClassRepository.findByDivisionIdOrderByDayOfWeekAscTimeSlotAsc(divisionId).stream()
                .map(ScheduledClassDTO::new)
                .collect(Collectors.toList());
    }
    public void deleteByDivision(Long divisionId) {
    List<ScheduledClass> classes = scheduledClassRepository.findByDivisionId(divisionId);
    scheduledClassRepository.deleteAll(classes);
}


    @Transactional(readOnly = true)
    public List<ScheduledClassDTO> getTimetableForTeacher(Long teacherId) {
        // THIS NOW CALLS THE CORRECT, RENAMED REPOSITORY METHOD
        return scheduledClassRepository.findByTeacherIdOrderByDayOfWeekAscTimeSlotAsc(teacherId).stream()
                .map(ScheduledClassDTO::new)
                .collect(Collectors.toList());
    }
}

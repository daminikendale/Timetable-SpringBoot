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
        List<ScheduledClass> list =
                scheduledClassRepository.findByDivision_IdOrderByDayOfWeekAscTimeSlot_StartTimeAsc(divisionId);

        return list.stream().map(ScheduledClassDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScheduledClassDTO> getTimetableForTeacher(Long teacherId) {
        List<ScheduledClass> list =
                scheduledClassRepository.findByTeacher_IdOrderByDayOfWeekAscTimeSlot_StartTimeAsc(teacherId);

        return list.stream().map(ScheduledClassDTO::new).collect(Collectors.toList());
    }

    @Transactional
    public void deleteByDivision(Long divisionId) {
        List<ScheduledClass> classes = scheduledClassRepository.findByDivision_Id(divisionId);
        scheduledClassRepository.deleteAll(classes);
    }
}

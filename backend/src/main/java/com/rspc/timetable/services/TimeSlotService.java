package com.rspc.timetable.services;

import com.rspc.timetable.dto.TimeSlotDTO;
import com.rspc.timetable.entities.TimeSlot;
import com.rspc.timetable.repositories.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;

    @Transactional(readOnly = true)
    public List<TimeSlotDTO> getAllTimeSlots() {
        return timeSlotRepository.findAllByOrderByStartTimeAsc()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TimeSlotDTO getTimeSlotById(Long id) {
        return timeSlotRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Transactional
    public TimeSlot createTimeSlot(TimeSlot timeSlot) {
        return timeSlotRepository.save(timeSlot);
    }

    @Transactional
    public List<TimeSlot> createTimeSlots(List<TimeSlot> timeSlots) {
        return timeSlotRepository.saveAll(timeSlots);
    }

    @Transactional
    public void deleteTimeSlot(Long id) {
        timeSlotRepository.deleteById(id);
    }

    private TimeSlotDTO convertToDTO(TimeSlot ts) {
        TimeSlotDTO dto = new TimeSlotDTO();
        dto.setId(ts.getId());
        dto.setStart_time(ts.getStartTime().toString());
        dto.setEnd_time(ts.getEndTime().toString());
        return dto;
    }
}

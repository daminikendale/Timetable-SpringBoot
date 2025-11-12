package com.rspc.timetable.services;

import com.rspc.timetable.entities.TimeSlot;
import com.rspc.timetable.repositories.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;

    @Transactional(readOnly = true)
    public List<TimeSlot> getAllTimeSlots() {
        return timeSlotRepository.findAll();
    }

    @Transactional(readOnly = true)
    public TimeSlot getTimeSlotById(Long id) {
        return timeSlotRepository.findById(id).orElse(null);
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
}

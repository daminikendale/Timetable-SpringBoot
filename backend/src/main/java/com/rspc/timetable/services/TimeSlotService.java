package com.rspc.timetable.services;

import com.rspc.timetable.entities.TimeSlot;
import com.rspc.timetable.repositories.TimeSlotRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;

    public TimeSlotService(TimeSlotRepository timeSlotRepository) {
        this.timeSlotRepository = timeSlotRepository;
    }

    // Get all timeslots
    public List<TimeSlot> getAllTimeSlots() {
        return timeSlotRepository.findAll();
    }

    // Get by ID
    public TimeSlot getTimeSlotById(Long id) {
        return timeSlotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TimeSlot not found: " + id));
    }

    // Create single timeslot
    public TimeSlot createTimeSlot(TimeSlot timeSlot) {
        return timeSlotRepository.save(timeSlot);
    }

    // Create multiple timeslots
    public List<TimeSlot> createTimeSlots(List<TimeSlot> timeSlots) {
        return timeSlotRepository.saveAll(timeSlots);
    }

    // Delete
    public void deleteTimeSlot(Long id) {
        timeSlotRepository.deleteById(id);
    }
}

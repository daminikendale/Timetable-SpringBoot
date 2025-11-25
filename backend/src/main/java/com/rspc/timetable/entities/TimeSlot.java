package com.rspc.timetable.entities;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "timeslots")
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "slot_number")
    private Integer slotNumber;

    // Optional: If you need to categorize slots
    // @Enumerated(EnumType.STRING)
    // @Column(name = "slot_type")
    // private SlotType slotType;

    // Constructors
    public TimeSlot() {}

    public TimeSlot(LocalTime startTime, LocalTime endTime, Integer slotNumber) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.slotNumber = slotNumber;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Integer getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(Integer slotNumber) {
        this.slotNumber = slotNumber;
    }
}

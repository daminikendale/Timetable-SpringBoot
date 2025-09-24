package com.rspc.timetable.entities;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "time_slots")
public class TimeSlot {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "start_time", nullable = false)
    private String startTime; // HH:mm format
    
    @Column(name = "end_time", nullable = false)
    private String endTime; // HH:mm format
    
    @Column(name = "day_of_week")
    private String dayOfWeek; // MON, TUE, WED, THU, FRI
    
    // Constructors
    public TimeSlot() {}
    
    public TimeSlot(String startTime, String endTime, String dayOfWeek) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.dayOfWeek = dayOfWeek;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getStartTime() {
        return startTime;
    }
    
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    
    public String getEndTime() {
        return endTime;
    }
    
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    
    public String getDayOfWeek() {
        return dayOfWeek;
    }
    
    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    
    // Helper method to check if this slot conflicts with break
    public boolean conflictsWith(String breakStartTime, String breakEndTime) {
        return !(this.endTime.compareTo(breakStartTime) <= 0 || 
                 this.startTime.compareTo(breakEndTime) >= 0);
    }
}
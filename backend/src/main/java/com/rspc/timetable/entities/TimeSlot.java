package com.rspc.timetable.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "timeslots")
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    /**
     * This field is crucial. It distinguishes between a schedulable class period
     * and a non-schedulable break (like lunch).
     */
    @Column(nullable = false)
    private boolean isBreak = false;
}

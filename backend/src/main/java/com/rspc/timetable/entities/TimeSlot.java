package com.rspc.timetable.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "timeslots")
@Getter @Setter
public class TimeSlot {

    public enum SlotType { REGULAR, SHORT_BREAK_CANDIDATE, LUNCH_CANDIDATE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SlotType type = SlotType.REGULAR;
}

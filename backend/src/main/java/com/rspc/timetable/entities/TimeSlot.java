package com.rspc.timetable.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "timeslots")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String startTime; // e.g. "09:00"
    private String endTime;   // e.g. "10:00"
}

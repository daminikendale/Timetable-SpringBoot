// src/main/java/com/rspc/timetable/entities/Timetable.java
package com.rspc.timetable.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "timetable") // ensure this matches your actual table name
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Timetable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne private Division division;
    @ManyToOne private Subject subject;
    @ManyToOne private Teacher teacher;
    @ManyToOne private Classroom classroom;
    @ManyToOne private TimeSlot timeSlot;

    private boolean isOverride;
    private boolean isElective;
    private String electiveGroup;

    private LocalDate overrideStartDate;
    private LocalDate overrideEndDate;

    // convenience ctor used by generator
    public Timetable(Division division, Subject subject, Teacher teacher, Classroom classroom,
                     TimeSlot timeSlot, boolean isOverride, boolean isElective,
                     String electiveGroup, LocalDate overrideStartDate, LocalDate overrideEndDate) {
        this.division = division;
        this.subject = subject;
        this.teacher = teacher;
        this.classroom = classroom;
        this.timeSlot = timeSlot;
        this.isOverride = isOverride;
        this.isElective = isElective;
        this.electiveGroup = electiveGroup;
        this.overrideStartDate = overrideStartDate;
        this.overrideEndDate = overrideEndDate;
    }
}

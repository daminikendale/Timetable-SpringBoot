// src/main/java/com/rspc/timetable/entities/Timetable.java
package com.rspc.timetable.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "timetable")
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

    // Add day because TimeSlot is a dayless time template
    @Column(name = "day", nullable = false, length = 8)
    private String day; // e.g., MON,TUE,WED,THU,FRI

    private boolean isOverride;
    private boolean isElective;
    private String electiveGroup;
    private LocalDate overrideStartDate;
    private LocalDate overrideEndDate;

    // Preferred constructor (used by new generator): includes day
    public Timetable(Division division, Subject subject, Teacher teacher, Classroom classroom,
                     TimeSlot timeSlot, String day,
                     boolean isOverride, boolean isElective, String electiveGroup,
                     LocalDate overrideStartDate, LocalDate overrideEndDate) {
        this.division = division;
        this.subject = subject;
        this.teacher = teacher;
        this.classroom = classroom;
        this.timeSlot = timeSlot;
        this.day = day;
        this.isOverride = isOverride;
        this.isElective = isElective;
        this.electiveGroup = electiveGroup;
        this.overrideStartDate = overrideStartDate;
        this.overrideEndDate = overrideEndDate;
    }

    // Legacy overload (keeps old call sites compiling): defaults day if not provided
    public Timetable(Division division, Subject subject, Teacher teacher, Classroom classroom,
                     TimeSlot timeSlot,
                     boolean isOverride, boolean isElective, String electiveGroup,
                     LocalDate overrideStartDate, LocalDate overrideEndDate) {
        this(division, subject, teacher, classroom, timeSlot, "MON",
             isOverride, isElective, electiveGroup, overrideStartDate, overrideEndDate);
    }
}

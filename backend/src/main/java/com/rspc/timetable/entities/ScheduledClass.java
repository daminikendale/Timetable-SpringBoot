package com.rspc.timetable.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "scheduled_classes")
public class ScheduledClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK: division_id → divisions.id
    @ManyToOne
    @JoinColumn(name = "division_id")
    private Division division;

    // FK: subject_id → subjects.id
    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    // FK: teacher_id → teachers.id
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    // FK: classroom_id → classrooms.id
    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    // FK: time_slot_id → timeslots.id
    @ManyToOne
    @JoinColumn(name = "time_slot_id")
    private TimeSlot timeSlot;

    // FK: batch_id → batches.id
    @ManyToOne
    @JoinColumn(name = "batch_id")
    private Batch batch;

    // FK: course_offering_id → course_offerings.id (🔥 FIXED)
    @ManyToOne
    @JoinColumn(name = "course_offering_id")
    private CourseOffering courseOffering;

    @Column(name = "session_type")
    private String sessionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    private java.time.DayOfWeek dayOfWeek;
}

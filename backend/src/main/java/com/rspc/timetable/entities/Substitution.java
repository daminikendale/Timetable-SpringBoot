package com.rspc.timetable.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "substitutions")
public class Substitution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheduled_class_id", nullable = false)
    private ScheduledClass scheduledClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_teacher_id", nullable = false)
    private Teacher originalTeacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "substitute_teacher_id", nullable = false)
    private Teacher substituteTeacher;

    @Column(nullable = false)
    private LocalDate substitutionDate;
}

package com.rspc.timetable.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "subjects")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    private String name;
    private int priority;

    @Enumerated(EnumType.STRING)
    private SubjectCategory category;

    // --- **THE FIX** ---
    // Add this relationship back. A Subject must belong to a Semester.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    public enum SubjectCategory {
        REGULAR,
        PROGRAM_ELECTIVE,
        OPEN_ELECTIVE
    }
}

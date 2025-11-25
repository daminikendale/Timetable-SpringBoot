package com.rspc.timetable.entities;

import jakarta.persistence.*;
import lombok.Data;
@Data
@Entity
@Table(name = "subjects")
public class Subject {

    public enum SubjectCategory {
        OPEN_ELECTIVE, PROGRAM_ELECTIVE, REGULAR
    }

    public enum SubjectType {
        LAB, THEORY, TUTORIAL
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int priority;

    @Column(nullable = false, unique = true)
    private String code;

    private String name;

    @Enumerated(EnumType.STRING)
    private SubjectCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "subject_type")
    private SubjectType subjectType;     // maps to your DB enum

    @ManyToOne(optional = false)
    @JoinColumn(name = "semester_id")
    private Semester semester;

    // getters/setters
}

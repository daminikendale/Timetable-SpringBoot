package com.rspc.timetable.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subjects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    // Category of the subject (e.g. REGULAR, OPEN_ELECTIVE, PROGRAM_ELECTIVE)
    @Enumerated(EnumType.STRING)
    private SubjectCategory category;

    // Links this subject to its semester
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    // Subject type (e.g., THEORY, LAB, TUTORIAL)
    @Enumerated(EnumType.STRING)
    private SubjectType subjectType;

    public enum SubjectType {
        THEORY,
        LAB,
        TUTORIAL
    }
}

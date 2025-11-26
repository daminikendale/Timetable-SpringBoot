package com.rspc.timetable.entities;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    // REGULAR, HONORS, ELECTIVE
    @Enumerated(EnumType.STRING)
    private SubjectCategory category;

    public enum SubjectCategory {
        REGULAR,
        HONORS,
        ELECTIVE
    }

    // THEORY, LAB, TUTORIAL
    @Enumerated(EnumType.STRING)
    private SubjectType type;

    public enum SubjectType {
        THEORY,
        LAB,
        TUTORIAL
    }

    @ManyToOne
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;
}

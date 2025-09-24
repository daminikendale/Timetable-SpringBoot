// src/main/java/com/rspc/timetable/entities/Subject.java
package com.rspc.timetable.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subject", uniqueConstraints = @UniqueConstraint(name = "uk_subject_code", columnNames = "code"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Subject {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String code;

    @Column(nullable = false, length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private SubjectType type;

    @Column(nullable = false)
    private Integer credits;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private SubjectCategory category;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "year_id", nullable = false)
    private Year year;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;
}

package com.rspc.timetable.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type; // THEORY or LAB
    private int credits;

    @Enumerated(EnumType.STRING)
    private SubjectCategory category; // REGULAR, PROGRAM_ELECTIVE, OPEN_ELECTIVE

    @ManyToOne
    @JoinColumn(name = "year_id", nullable = false)
    @JsonIgnoreProperties({"semesters", "hibernateLazyInitializer", "handler"})
    private Year year;

    @ManyToOne
    @JoinColumn(name = "semester_id", nullable = false)
    @JsonIgnoreProperties({"year", "hibernateLazyInitializer", "handler"})
    private Semester semester;
}

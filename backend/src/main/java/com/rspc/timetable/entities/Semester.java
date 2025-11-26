package com.rspc.timetable.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "semesters")
public class Semester {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // REQUIRED — used by DTO: semester.getSemesterNumber()
    @Column(nullable = false)
    private int semesterNumber;

    // REQUIRED — used by DTO: semester.getYear()
    @ManyToOne
    @JoinColumn(name = "year_id", nullable = false)
    private Year year;

    public Semester() {}

    public Semester(int semesterNumber, Year year) {
        this.semesterNumber = semesterNumber;
        this.year = year;
    }
}

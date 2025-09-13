package com.rspc.timetable.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "semesters")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Semester {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int semesterNumber; // 1 or 2

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "year_id", nullable = false)
    @JsonIgnoreProperties({"semesters", "hibernateLazyInitializer", "handler"})
    private Year year;

    public Semester() {}

    public Semester(int semesterNumber, Year year) {
        this.semesterNumber = semesterNumber;
        this.year = year;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getSemesterNumber() { return semesterNumber; }
    public void setSemesterNumber(int semesterNumber) { this.semesterNumber = semesterNumber; }

    public Year getYear() { return year; }
    public void setYear(Year year) { this.year = year; }
}

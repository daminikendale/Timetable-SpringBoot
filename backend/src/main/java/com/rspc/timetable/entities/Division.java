package com.rspc.timetable.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "divisions")
public class Division {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // e.g., "2nd Year A"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "year_id")
    private Year year;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id")
    private Semester semester;

    public Division() {}

    public Division(String name, Year year, Semester semester) {
        this.name = name;
        this.year = year;
        this.semester = semester;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Year getYear() { return year; }
    public void setYear(Year year) { this.year = year; }

    public Semester getSemester() { return semester; }
    public void setSemester(Semester semester) { this.semester = semester; }
}

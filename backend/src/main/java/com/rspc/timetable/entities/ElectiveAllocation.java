package com.rspc.timetable.entities;

import jakarta.persistence.*;
@Entity
public class ElectiveAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "year_id")
    private Year year;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id")
    private Semester semester;

    private int credits;

    private String divisionName;  // new field for E1, E2, etc.

    public ElectiveAllocation() {}

    // Updated constructor
    public ElectiveAllocation(Subject subject, String divisionName, int credits, Year year, Semester semester, Teacher teacher) {
        this.subject = subject;
        this.divisionName = divisionName;
        this.credits = credits;
        this.year = year;
        this.semester = semester;
        this.teacher = teacher;
    }

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }

    public Year getYear() { return year; }
    public void setYear(Year year) { this.year = year; }

    public Semester getSemester() { return semester; }
    public void setSemester(Semester semester) { this.semester = semester; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public String getDivisionName() { return divisionName; }
    public void setDivisionName(String divisionName) { this.divisionName = divisionName; }
}

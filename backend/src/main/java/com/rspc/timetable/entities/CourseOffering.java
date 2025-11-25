package com.rspc.timetable.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "course_offerings")
public class CourseOffering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "year_id")
    private Year year;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "semester_id")
    private Semester semester;

    // Workload
    @Column(name = "lec_per_week")
    private Integer lecPerWeek;

    @Column(name = "tut_per_week")
    private Integer tutPerWeek;

    @Column(name = "lab_per_week")
    private Integer labPerWeek;

    @Column(name = "weekly_hours")
    private Integer weeklyHours;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public Year getYear() { return year; }
    public void setYear(Year year) { this.year = year; }

    public Semester getSemester() { return semester; }
    public void setSemester(Semester semester) { this.semester = semester; }

    public Integer getLecPerWeek() { return lecPerWeek; }
    public void setLecPerWeek(Integer lecPerWeek) { this.lecPerWeek = lecPerWeek; }

    public Integer getTutPerWeek() { return tutPerWeek; }
    public void setTutPerWeek(Integer tutPerWeek) { this.tutPerWeek = tutPerWeek; }

    public Integer getLabPerWeek() { return labPerWeek; }
    public void setLabPerWeek(Integer labPerWeek) { this.labPerWeek = labPerWeek; }

    public Integer getWeeklyHours() { return weeklyHours; }
    public void setWeeklyHours(Integer weeklyHours) { this.weeklyHours = weeklyHours; }
}

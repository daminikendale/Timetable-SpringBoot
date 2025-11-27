package com.rspc.timetable.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "course_offerings")
public class CourseOffering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "weekly_hours", nullable = false)
    private Integer weeklyHours;

    @ManyToOne
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "session_type")
    private String sessionType;

    @Column(name = "lab_per_week", nullable = false)
    private Integer labPerWeek;

    @Column(name = "lec_per_week", nullable = false)
    private Integer lecPerWeek;

    @Column(name = "tut_per_week", nullable = false)
    private Integer tutPerWeek;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department; // nullable per DESCRIBE

    @ManyToOne
@JoinColumn(name = "year_id", nullable = false)
private Year year;

    public CourseOffering() { }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getWeeklyHours() { return weeklyHours; }
    public void setWeeklyHours(Integer weeklyHours) { this.weeklyHours = weeklyHours; }

    public Semester getSemester() { return semester; }
    public void setSemester(Semester semester) { this.semester = semester; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public String getSessionType() { return sessionType; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }

    public Integer getLabPerWeek() { return labPerWeek; }
    public void setLabPerWeek(Integer labPerWeek) { this.labPerWeek = labPerWeek; }

    public Integer getLecPerWeek() { return lecPerWeek; }
    public void setLecPerWeek(Integer lecPerWeek) { this.lecPerWeek = lecPerWeek; }

    public Integer getTutPerWeek() { return tutPerWeek; }
    public void setTutPerWeek(Integer tutPerWeek) { this.tutPerWeek = tutPerWeek; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

   public Year getYear() { return year; }
public void setYear(Year year) { this.year = year; }

}

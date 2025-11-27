package com.rspc.timetable.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "course_offerings")
public class CourseOffering {

     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer lecPerWeek;
    private Integer tutPerWeek;
    private Integer labPerWeek;
    private Integer weeklyHours;
    private Integer totalHours;

    @ManyToOne
    @JoinColumn(name = "semester_id")
    private Semester semester;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "year_id")
    private Year year;

    private String sessionType;

    // existing getters/setters...

    public Department getDepartment() {
        return department;
    }
    public void setDepartment(Department department) {
        this.department = department;
    }

    public Year getYear() {
        return year;
    }
    public void setYear(Year year) {
        this.year = year;
    }

    // getters + setters
    public Long getId() { return id; }

    public Integer getLecPerWeek() { return lecPerWeek; }
    public Integer getTutPerWeek() { return tutPerWeek; }
    public Integer getLabPerWeek() { return labPerWeek; }

    public Integer getWeeklyHours() { return weeklyHours; }
    public Integer getTotalHours() { return totalHours; }

    public Semester getSemester() { return semester; }
    public Subject getSubject() { return subject; }

    public void setId(Long id) { this.id = id; }
    public void setLecPerWeek(Integer lecPerWeek) { this.lecPerWeek = lecPerWeek; }
    public void setTutPerWeek(Integer tutPerWeek) { this.tutPerWeek = tutPerWeek; }
    public void setLabPerWeek(Integer labPerWeek) { this.labPerWeek = labPerWeek; }
    public void setWeeklyHours(Integer weeklyHours) { this.weeklyHours = weeklyHours; }
    public void setTotalHours(Integer totalHours) { this.totalHours = totalHours; }
    public void setSemester(Semester semester) { this.semester = semester; }
    public void setSubject(Subject subject) { this.subject = subject; }
}

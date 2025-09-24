package com.rspc.timetable.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "course_offerings")
public class CourseOffering {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject_id", nullable = false)
    private Long subjectId;

    @Column(name = "year_id", nullable = false)
    private Long yearId;

    @Column(name = "semester_id", nullable = false)
    private Long semesterId;

    @Column(name = "division_id")
    private Long divisionId;

    @Column(name = "lec_per_week", nullable = false)
    private Integer lecPerWeek = 0;

    @Column(name = "tut_per_week", nullable = false)
    private Integer tutPerWeek = 0;

    @Column(name = "lab_per_week", nullable = false)
    private Integer labPerWeek = 0;

    @Column(name = "is_elective", nullable = false)
    private Boolean isElective = false;

    @Column(name = "elective_group_id")
    private Long electiveGroupId;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public Long getYearId() { return yearId; }
    public void setYearId(Long yearId) { this.yearId = yearId; }

    public Long getSemesterId() { return semesterId; }
    public void setSemesterId(Long semesterId) { this.semesterId = semesterId; }

    public Long getDivisionId() { return divisionId; }
    public void setDivisionId(Long divisionId) { this.divisionId = divisionId; }

    public Integer getLecPerWeek() { return lecPerWeek; }
    public void setLecPerWeek(Integer lecPerWeek) { this.lecPerWeek = lecPerWeek; }

    public Integer getTutPerWeek() { return tutPerWeek; }
    public void setTutPerWeek(Integer tutPerWeek) { this.tutPerWeek = tutPerWeek; }

    public Integer getLabPerWeek() { return labPerWeek; }
    public void setLabPerWeek(Integer labPerWeek) { this.labPerWeek = labPerWeek; }

    public Boolean getIsElective() { return isElective; }
    public void setIsElective(Boolean isElective) { this.isElective = isElective; }

    public Long getElectiveGroupId() { return electiveGroupId; }
    public void setElectiveGroupId(Long electiveGroupId) { this.electiveGroupId = electiveGroupId; }
}

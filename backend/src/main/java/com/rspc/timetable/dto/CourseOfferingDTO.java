package com.rspc.timetable.dto;

public class CourseOfferingDTO {

    private Long id;

    private Long subjectId;
    private Long departmentId;
    private Long yearId;
    private Long semesterId;

    private Integer lecPerWeek;
    private Integer tutPerWeek;
    private Integer labPerWeek;
    private Integer weeklyHours;

    public CourseOfferingDTO() {}

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public Long getYearId() { return yearId; }
    public void setYearId(Long yearId) { this.yearId = yearId; }

    public Long getSemesterId() { return semesterId; }
    public void setSemesterId(Long semesterId) { this.semesterId = semesterId; }

    public Integer getLecPerWeek() { return lecPerWeek; }
    public void setLecPerWeek(Integer lecPerWeek) { this.lecPerWeek = lecPerWeek; }

    public Integer getTutPerWeek() { return tutPerWeek; }
    public void setTutPerWeek(Integer tutPerWeek) { this.tutPerWeek = tutPerWeek; }

    public Integer getLabPerWeek() { return labPerWeek; }
    public void setLabPerWeek(Integer labPerWeek) { this.labPerWeek = labPerWeek; }

    public Integer getWeeklyHours() { return weeklyHours; }
    public void setWeeklyHours(Integer weeklyHours) { this.weeklyHours = weeklyHours; }
}

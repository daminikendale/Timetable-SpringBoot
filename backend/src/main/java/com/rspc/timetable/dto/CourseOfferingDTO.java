package com.rspc.timetable.dto;

import com.rspc.timetable.entities.CourseOffering;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CourseOfferingDTO {

    private Long id;
    private Long subjectId;
    private String subjectName;
    private Long departmentId;
    private String departmentName;
    private Long yearId;
    private Long semesterId;
    private Integer semesterNumber;
    private Integer lecPerWeek;
    private Integer tutPerWeek;
    private Integer labPerWeek;
    private Integer weeklyHours;

    public CourseOfferingDTO(CourseOffering offering) {
        this.id = offering.getId();
        this.subjectId = offering.getSubject().getId();
        this.subjectName = offering.getSubject().getName();
        this.departmentId = offering.getDepartment().getId();
        this.departmentName = offering.getDepartment().getName();
        this.yearId = offering.getYear().getId();
        this.semesterId = offering.getSemester().getId();
        this.semesterNumber = offering.getSemester().getSemesterNumber();
        this.lecPerWeek = offering.getLecPerWeek();
        this.tutPerWeek = offering.getTutPerWeek();
        this.labPerWeek = offering.getLabPerWeek();
        this.weeklyHours = offering.getWeeklyHours();
    }
}

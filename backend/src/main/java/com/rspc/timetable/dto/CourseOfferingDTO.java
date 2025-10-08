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
    private Long yearId;
    private Long semesterId;
    private Integer lecPerWeek;
    private Integer tutPerWeek;
    private Integer labPerWeek;

    /**
     * Constructor to map from a CourseOffering entity to this DTO.
     * This is used throughout the service layer for consistent mapping.
     */
    public CourseOfferingDTO(CourseOffering offering) {
        this.id = offering.getId();
        this.subjectId = offering.getSubject().getId();
        this.subjectName = offering.getSubject().getName();
        this.departmentId = offering.getDepartment().getId();
        this.yearId = offering.getYear().getId();
        this.semesterId = offering.getSemester().getId();
        this.lecPerWeek = offering.getLecPerWeek();
        this.tutPerWeek = offering.getTutPerWeek();
        this.labPerWeek = offering.getLabPerWeek();
    }
}

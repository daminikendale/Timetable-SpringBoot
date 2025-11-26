package com.rspc.timetable.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseOfferingDTO {

    private Long id;

    private Long subjectId;
    private Long departmentId;
    private Long yearId;
    private Long semesterId;

    private int lecPerWeek;
    private int tutPerWeek;
    private int labPerWeek;
    private int weeklyHours;
}

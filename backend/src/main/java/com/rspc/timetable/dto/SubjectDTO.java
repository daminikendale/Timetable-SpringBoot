// src/main/java/com/rspc/timetable/dto/SubjectDTO.java
package com.rspc.timetable.dto;

import lombok.Data;

@Data
public class SubjectDTO {
    private String code;
    private String name;
    private String type;       // string from JSON ("THEORY", "LAB", etc.)
    private Integer credits;
    private String category;   // string from JSON ("REGULAR", "PROGRAM_ELECTIVE", etc.)
    private Long yearId;
    private Long semesterId;
}

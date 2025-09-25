// src/main/java/com/rspc/timetable/dto/SubjectDTO.java
package com.rspc.timetable.dto;

import lombok.Data;

@Data
public class SubjectDTO {
    private String code;
    private String name;
    private String type;        // "THEORY" | "LAB"
    private Integer credits;
    private String category;    // "REGULAR" | "PROGRAM_ELECTIVE" | "OPEN_ELECTIVE"
    private Long yearId;
    private Long semesterId;
    private Integer priority;   // lower = tougher/earlier (optional)
}

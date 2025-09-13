package com.rspc.timetable.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SemesterDTO {
    private Long id;
    private int semesterNumber;
    private Long year_id;
    private Integer yearNumber;
}

package com.rspc.timetable.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor  // ✅ fixes the default constructor issue
@AllArgsConstructor
@Builder
public class SubjectDTO {
    private Long id;
    private String name;
    private String type;     // THEORY or LAB
    private int credits;
    private String category; // REGULAR, PROGRAM_ELECTIVE, OPEN_ELECTIVE
    private Long year_id;
    private Long semester_id;
}

package com.rspc.timetable.dto;

import com.rspc.timetable.entities.Semester;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SemesterDTO {

    private Long id;
    private int semesterNumber;
    private Long yearId;
    private String yearName; // Changed from yearNumber to yearName

    /**
     * Convenience constructor to map from the entity.
     */
    public SemesterDTO(Semester semester) {
        this.id = semester.getId();
        this.semesterNumber = semester.getSemesterNumber();
        if (semester.getYear() != null) {
            this.yearId = semester.getYear().getId();
            this.yearName = semester.getYear().getYearName(); // Uses the correct method
        }
    }
}

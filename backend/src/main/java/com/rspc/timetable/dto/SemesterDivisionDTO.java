package com.rspc.timetable.dto;

import com.rspc.timetable.entities.SemesterDivision;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SemesterDivisionDTO {
    private Long id;
    private Long semesterId;
    private Long divisionId;
    private String divisionName;

    // Constructor to map from the entity
    public SemesterDivisionDTO(SemesterDivision semesterDivision) {
        this.id = semesterDivision.getId();
        this.semesterId = semesterDivision.getSemester().getId();
        this.divisionId = semesterDivision.getDivision().getId();
        this.divisionName = semesterDivision.getDivision().getDivisionName();
    }
}

package com.rspc.timetable.dto;

import com.rspc.timetable.entities.Division;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DivisionDTO {
    private Long id;
    private String divisionName;

    public DivisionDTO(Division division) {
        if (division != null) {
            this.id = division.getId();
            this.divisionName = division.getDivisionName();
        }
    }
}

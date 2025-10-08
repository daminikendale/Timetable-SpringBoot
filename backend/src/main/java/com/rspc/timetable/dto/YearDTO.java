package com.rspc.timetable.dto;

import com.rspc.timetable.entities.Year;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YearDTO {

    private Long id;
    private String yearName; // Changed from yearNumber

    /**
     * Convenience constructor to map from the entity.
     */
    public YearDTO(Year year) {
        this.id = year.getId();
        this.yearName = year.getYearName(); // Uses the correct getter
    }
}

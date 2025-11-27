package com.rspc.timetable.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SolveResultDTO {
    private Long semesterId;
    private int plannedCount;
    private String message;
}

package com.rspc.timetable.dto;

import lombok.Data;

@Data
public class TimeSlotDTO {
    private Long id;
    private String start_time;
    private String end_time;
}

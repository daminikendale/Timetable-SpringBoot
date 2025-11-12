package com.rspc.timetable.dto;

import lombok.Data;

@Data
public class BatchDTO {
    private Long id;
    private String batchName;  // ✅ must match entity
    private int size;
    private Long divisionId;
}

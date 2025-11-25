package com.rspc.timetable.dto;

import lombok.Data;

@Data
public class BatchDTO {

    private Long id;
    private String batchName;
    private Integer batchNumber;
    private Integer size;
    private Long divisionId;
}

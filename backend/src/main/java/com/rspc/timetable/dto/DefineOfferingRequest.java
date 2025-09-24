package com.rspc.timetable.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DefineOfferingRequest {
    @NotBlank
    private String subjectCode;

    @NotNull
    @JsonProperty("yearid")
    private Long yearId;

    @NotNull
    @JsonProperty("semesterid")
    private Long semesterId;

    @JsonProperty("divisionid")
    private Long divisionId;

    @NotNull
    private Integer lecPerWeek;

    @NotNull
    private Integer tutPerWeek;

    @NotNull
    private Integer labPerWeek;

    @JsonProperty("is_elective")
    private Boolean isElective = false;

    @JsonProperty("elective_group_id")
    private Long electiveGroupId;
}

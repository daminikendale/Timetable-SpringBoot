package com.rspc.timetable.dto;

import lombok.Data;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

@Data
public class OfferingBulkRequest {

    @Valid
    @NotEmpty
    private List<DefineOfferingRequest> offerings;

    public static record BulkOfferingRequest(
  Long subjectId, Long yearId, Long semesterId,
  Integer lecPerWeek, Integer tutPerWeek, Integer labPerWeek,
  Boolean isElective, Long electiveGroupId
) {}
}

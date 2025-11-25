package com.rspc.timetable.dto;

import com.rspc.timetable.entities.SemesterDivision;

public class SemesterDivisionDTO {

    private Long id;
    private Long semesterId;
    private Long divisionId;

    public SemesterDivisionDTO() {
    }

    public SemesterDivisionDTO(Long semesterId, Long divisionId) {
        this.semesterId = semesterId;
        this.divisionId = divisionId;
    }

    // NEW: constructor used in SemesterDivisionService
    public SemesterDivisionDTO(SemesterDivision entity) {
        this.id = entity.getId();
        this.semesterId = entity.getSemester() != null ? entity.getSemester().getId() : null;
        this.divisionId = entity.getDivision() != null ? entity.getDivision().getId() : null;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSemesterId() { return semesterId; }
    public void setSemesterId(Long semesterId) { this.semesterId = semesterId; }

    public Long getDivisionId() { return divisionId; }
    public void setDivisionId(Long divisionId) { this.divisionId = divisionId; }
}

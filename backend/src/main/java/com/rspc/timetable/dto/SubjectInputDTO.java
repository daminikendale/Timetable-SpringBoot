package com.rspc.timetable.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SubjectInputDTO {

    private String name;
    private String type;  // THEORY / LAB
    private int credits;

    @JsonProperty("is_elective")
    private boolean elective;

    private Long year_id;
    private Long semester_id;

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public boolean isElective() { return elective; }
    public void setElective(boolean elective) { this.elective = elective; }

    public Long getYear_id() { return year_id; }
    public void setYear_id(Long year_id) { this.year_id = year_id; }

    public Long getSemester_id() { return semester_id; }
    public void setSemester_id(Long semester_id) { this.semester_id = semester_id; }
}

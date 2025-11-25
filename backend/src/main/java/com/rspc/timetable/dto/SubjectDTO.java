package com.rspc.timetable.dto;

import com.rspc.timetable.entities.Subject;

public class SubjectDTO {

    private Long id;
    private String name;
    private String code;
    private Subject.SubjectCategory category; 
    private Subject.SubjectType subjectType;      // <-- FIXED: use enum directly
    private Long semesterId;

    public SubjectDTO() {}

    public SubjectDTO(Subject entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.code = entity.getCode();
        this.category = entity.getCategory();
        this.subjectType = entity.getSubjectType();     // enum direct
        this.semesterId = entity.getSemester() != null 
                ? entity.getSemester().getId() : null;
    }

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Subject.SubjectCategory getCategory() { return category; }
    public void setCategory(Subject.SubjectCategory category) { this.category = category; }

    public Subject.SubjectType getSubjectType() { return subjectType; }
    public void setSubjectType(Subject.SubjectType subjectType) { this.subjectType = subjectType; }

    public Long getSemesterId() { return semesterId; }
    public void setSemesterId(Long semesterId) { this.semesterId = semesterId; }
}

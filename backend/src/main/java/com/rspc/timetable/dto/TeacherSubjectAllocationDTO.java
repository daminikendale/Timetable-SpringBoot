package com.rspc.timetable.dto;

public class TeacherSubjectAllocationDTO {
    private Long id;
    private Long teacherId;
    private Long subjectId;
    private Integer priority;

    public TeacherSubjectAllocationDTO() {}

    public TeacherSubjectAllocationDTO(Long id, Long teacherId, Long subjectId, Integer priority) {
        this.id = id;
        this.teacherId = teacherId;
        this.subjectId = subjectId;
        this.priority = priority;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}

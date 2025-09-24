// src/main/java/com/rspc/timetable/dto/TeacherSubjectAllocationDTO.java
package com.rspc.timetable.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TeacherSubjectAllocationDTO {
  @JsonProperty("subject_id") private Long subjectId;
  @JsonProperty("teacher_id") private Long teacherId;
  @JsonProperty("year_id")    private Long yearId;

  @JsonProperty("semester_id")   private Long semesterId;
  @JsonProperty("role")          private String role; // "THEORY" | "LAB"
  @JsonProperty("priority")      private Integer priority;
  @JsonProperty("capacity_hours")private Integer capacityHours;
  @JsonProperty("notes")         private String notes;

  public Long getSubjectId() { return subjectId; }
  public Long getTeacherId() { return teacherId; }
  public Long getYearId()    { return yearId; }
  public Long getSemesterId(){ return semesterId; }
  public String getRole()    { return role; }
  public Integer getPriority(){ return priority; }
  public Integer getCapacityHours(){ return capacityHours; }
  public String getNotes()   { return notes; }
}

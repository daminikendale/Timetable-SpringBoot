// src/main/java/com/rspc/timetable/dto/TimetableChangeRequest.java
package com.rspc.timetable.dto;

import java.time.LocalDate;
import java.util.List;

public class TimetableChangeRequest {
    private ChangeType type;

    private Long divisionId;            // required
    private LocalDate dateFrom;         // required for TEMPORARY
    private LocalDate dateTo;           // optional; if null, equals dateFrom

    private Long subjectId;             // optional filter
    private Long fromTeacherId;         // optional filter for TEMPORARY replace
    private Long toTeacherId;           // target teacher for both kinds
    private Long classroomId;           // optional target classroom
    private List<Long> timeSlotIds;     // optional subset of slots; null = all  


    
    public ChangeType getType() {
        return type;
    }
    public void setType(ChangeType type) {
        this.type = type;
    }
    public Long getDivisionId() {
        return divisionId;
    }
    public void setDivisionId(Long divisionId) {
        this.divisionId = divisionId;
    }
    public LocalDate getDateFrom() {
        return dateFrom;
    }
    public void setDateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
    }
    public LocalDate getDateTo() {
        return dateTo;
    }
    public void setDateTo(LocalDate dateTo) {
        this.dateTo = dateTo;
    }
    public Long getSubjectId() {
        return subjectId;
    }
    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }
    public Long getFromTeacherId() {
        return fromTeacherId;
    }
    public void setFromTeacherId(Long fromTeacherId) {
        this.fromTeacherId = fromTeacherId;
    }
    public Long getToTeacherId() {
        return toTeacherId;
    }
    public void setToTeacherId(Long toTeacherId) {
        this.toTeacherId = toTeacherId;
    }
    public Long getClassroomId() {
        return classroomId;
    }
    public void setClassroomId(Long classroomId) {
        this.classroomId = classroomId;
    }
    public List<Long> getTimeSlotIds() {
        return timeSlotIds;
    }
    public void setTimeSlotIds(List<Long> timeSlotIds) {
        this.timeSlotIds = timeSlotIds;
    }

    
}

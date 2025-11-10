package com.rspc.timetable.dto;

public class CreateScheduledClassRequest {
    private String dayOfWeek;
    private String sessionType;
    private Long divisionId;
    private Long timeslotId;
    private Long courseOfferingId;
    private Long teacherId;
    private Long classroomId;

    // Getters and Setters (or use Lombok @Data for auto-generation)
    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public String getSessionType() { return sessionType; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }

    public Long getDivisionId() { return divisionId; }
    public void setDivisionId(Long divisionId) { this.divisionId = divisionId; }

    public Long getTimeslotId() { return timeslotId; }
    public void setTimeslotId(Long timeslotId) { this.timeslotId = timeslotId; }

    public Long getCourseOfferingId() { return courseOfferingId; }
    public void setCourseOfferingId(Long courseOfferingId) { this.courseOfferingId = courseOfferingId; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public Long getClassroomId() { return classroomId; }
    public void setClassroomId(Long classroomId) { this.classroomId = classroomId; }
}

package com.rspc.timetable.dto;

import com.rspc.timetable.entities.ScheduledClass;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ScheduledClassDTO {

    private Long id;
    private String dayOfWeek;

    private String timeSlot; // "HH:mm - HH:mm"
    private String startTime;
    private String endTime;

    private String subjectName;
    private String subjectCode;

    private String teacherName;
    private String classroomName;

    private String sessionType;
    private String divisionName;
    private String batchName;

    public ScheduledClassDTO(ScheduledClass sc) {
        this.id = sc.getId();
        this.dayOfWeek = sc.getDayOfWeek().name();

        if (sc.getTimeSlot() != null) {
            this.startTime = sc.getTimeSlot().getStartTime().toString();
            this.endTime = sc.getTimeSlot().getEndTime().toString();
            this.timeSlot = startTime.substring(0,5) + " - " + endTime.substring(0,5);
        }

        if (sc.getSubject() != null) {
            this.subjectName = sc.getSubject().getName();
            this.subjectCode = sc.getSubject().getCode();
        }

        this.teacherName = (sc.getTeacher() != null) ? sc.getTeacher().getName() : null;
        this.classroomName = (sc.getClassroom() != null) ? sc.getClassroom().getName() : null;
        this.divisionName = (sc.getDivision() != null) ? sc.getDivision().getDivisionName() : null;
        this.sessionType = sc.getSessionType();
        this.batchName = (sc.getBatch() != null) ? sc.getBatch().getBatchName() : null;
    }
}

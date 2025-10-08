package com.rspc.timetable.dto;

import com.rspc.timetable.entities.ScheduledClass;
import lombok.Data;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;

@Data
public class ScheduledClassDTO {

    private Long id;
    private DayOfWeek dayOfWeek;
    private String timeSlot;
    private String subjectName;
    private String subjectCode;
    private String teacherName;
    private String classroomNumber;
    private String sessionType;
    private String divisionName;

    public ScheduledClassDTO(ScheduledClass sc) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        this.id = sc.getId();
        this.dayOfWeek = sc.getDayOfWeek();
        this.timeSlot = sc.getTimeSlot().getStartTime().format(timeFormatter) + " - " + sc.getTimeSlot().getEndTime().format(timeFormatter);
        this.subjectName = sc.getCourseOffering().getSubject().getName();
        this.subjectCode = sc.getCourseOffering().getSubject().getCode();
        this.classroomNumber = sc.getClassroom().getRoomNumber();
        this.divisionName = sc.getDivision().getDivisionName();
        this.sessionType = sc.getSessionType().toString();

        // --- **THE FIX** ---
        // This now reads the teacher's name from the ScheduledClass entity directly,
        // which has its own relationship to the Teacher.
        if (sc.getTeacher() != null) {
            this.teacherName = sc.getTeacher().getName();
        }
    }
}

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

        // 🕒 Handle time slot safely
        if (sc.getTimeSlot() != null && sc.getTimeSlot().getStartTime() != null && sc.getTimeSlot().getEndTime() != null) {
            this.timeSlot = sc.getTimeSlot().getStartTime().format(timeFormatter)
                    + " - " + sc.getTimeSlot().getEndTime().format(timeFormatter);
        }

        // 📘 Subject details
        if (sc.getCourseOffering() != null && sc.getCourseOffering().getSubject() != null) {
            this.subjectName = sc.getCourseOffering().getSubject().getName();
            this.subjectCode = sc.getCourseOffering().getSubject().getCode();
        }

        // 🧑‍🏫 Teacher
        if (sc.getTeacher() != null) {
            this.teacherName = sc.getTeacher().getName();
        } else {
            this.teacherName = "TBD"; // fallback
        }

        // 🏫 Classroom
        if (sc.getClassroom() != null) {
            this.classroomNumber = sc.getClassroom().getRoomNumber();
        }

        // 🏷 Division
        if (sc.getDivision() != null) {
            this.divisionName = sc.getDivision().getDivisionName();
        }

        // 🧩 Session type (THEORY / LAB / TUTORIAL)
        if (sc.getSessionType() != null) {
            this.sessionType = sc.getSessionType().toString();
        }
    }
}

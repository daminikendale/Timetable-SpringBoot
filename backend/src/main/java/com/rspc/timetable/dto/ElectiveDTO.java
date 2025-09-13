package com.rspc.timetable.dto;

import java.util.List;

public class ElectiveDTO {
    private String timeSlot;         // e.g., "Monday 09:00-10:00"
    private String classroom;        // optional or primary classroom
    private List<String> subjects;   // elective subject names
    private List<String> teachers;   // corresponding teachers

    public ElectiveDTO(String timeSlot, String classroom, List<String> subjects, List<String> teachers) {
        this.timeSlot = timeSlot;
        this.classroom = classroom;
        this.subjects = subjects;
        this.teachers = teachers;
    }

    // Getters & Setters
    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public String getClassroom() { return classroom; }
    public void setClassroom(String classroom) { this.classroom = classroom; }

    public List<String> getSubjects() { return subjects; }
    public void setSubjects(List<String> subjects) { this.subjects = subjects; }

    public List<String> getTeachers() { return teachers; }
    public void setTeachers(List<String> teachers) { this.teachers = teachers; }
}

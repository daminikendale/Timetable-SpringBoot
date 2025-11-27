package com.rspc.timetable.optaplanner;

import com.rspc.timetable.entities.*;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class PlannedClass {

    @PlanningId
    private Long id;

    private CourseOffering offering;
    private Subject subject;
    private Division division;
    private Batch batch;          // NULL for lectures
    private String sessionType;   // LECTURE / LAB / TUTORIAL
    private Integer hours;

    // Planning variables
    @PlanningVariable(valueRangeProviderRefs = {"timeSlotRange"})
    private TimeSlot timeSlot;

    @PlanningVariable(valueRangeProviderRefs = {"roomRange"})
    private Classroom room;

    // NOT A PLANNING VARIABLE → assigned before solving
    private Teacher teacher;

    public PlannedClass() {}

    // ---------------------------
    // Getters and Setters
    // ---------------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public CourseOffering getOffering() { return offering; }
    public void setOffering(CourseOffering offering) { this.offering = offering; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public Division getDivision() { return division; }
    public void setDivision(Division division) { this.division = division; }

    public Batch getBatch() { return batch; }
    public void setBatch(Batch batch) { this.batch = batch; }

    public String getSessionType() { return sessionType; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }

    public Integer getHours() { return hours; }
    public void setHours(Integer hours) { this.hours = hours; }

    public TimeSlot getTimeSlot() { return timeSlot; }
    public void setTimeSlot(TimeSlot timeSlot) { this.timeSlot = timeSlot; }

    public Classroom getRoom() { return room; }
    public void setRoom(Classroom room) { this.room = room; }

    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }

    // convenience
    public boolean isLecture() { return "LECTURE".equalsIgnoreCase(sessionType); }
    public boolean isTutorial() { return "TUTORIAL".equalsIgnoreCase(sessionType); }
    public boolean isLab() { return "LAB".equalsIgnoreCase(sessionType); }
}
//comment 
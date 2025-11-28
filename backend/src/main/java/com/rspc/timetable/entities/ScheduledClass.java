package com.rspc.timetable.entities;

import jakarta.persistence.*;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@Entity
@Table(name = "scheduled_classes")
@PlanningEntity
public class ScheduledClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ----- FIXED PLANNING VARIABLES -----

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    @PlanningVariable(valueRangeProviderRefs = "teacherRange")
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "classroom_id")
    @PlanningVariable(valueRangeProviderRefs = "roomRange")
    private Classroom classroom;

    @ManyToOne
    @JoinColumn(name = "time_slot_id")
    @PlanningVariable(valueRangeProviderRefs = "timeSlotRange")
    private TimeSlot timeSlot;

    // ----- NORMAL (problem facts) -----
    @ManyToOne
    @JoinColumn(name = "division_id", nullable = false)
    private Division division;

    @ManyToOne
    @JoinColumn(name = "course_offering_id")
    private CourseOffering courseOffering;

    @ManyToOne
    @JoinColumn(name = "batch_id")
    private Batch batch;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @Column(name = "day_of_week", nullable = false)
    private String dayOfWeek;

    @Column(name = "session_type", nullable = false)
    private String sessionType;

    @Column(name = "scheduling_level")
    private String schedulingLevel;

    @Transient
    private Integer semesterNumber;

    // Getters + Setters (NO CHANGE)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }

    public Classroom getClassroom() { return classroom; }
    public void setClassroom(Classroom classroom) { this.classroom = classroom; }

    public TimeSlot getTimeSlot() { return timeSlot; }
    public void setTimeSlot(TimeSlot timeSlot) { this.timeSlot = timeSlot; }

    public Division getDivision() { return division; }
    public void setDivision(Division division) { this.division = division; }

    public CourseOffering getCourseOffering() { return courseOffering; }
    public void setCourseOffering(CourseOffering courseOffering) { this.courseOffering = courseOffering; }

    public Batch getBatch() { return batch; }
    public void setBatch(Batch batch) { this.batch = batch; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public String getSessionType() { return sessionType; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }

    public Integer getSemesterNumber() { return semesterNumber; }
    public void setSemesterNumber(Integer semesterNumber) { this.semesterNumber = semesterNumber; }

    public String getSchedulingLevel() { return schedulingLevel; }
    public void setSchedulingLevel(String schedulingLevel) { this.schedulingLevel = schedulingLevel; }
}

package com.rspc.timetable.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "scheduled_classes")
public class ScheduledClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    @ManyToOne
    @JoinColumn(name = "course_offering_id")
    private CourseOffering courseOffering;

    @ManyToOne
    @JoinColumn(name = "division_id", nullable = false)
    private Division division;

    // String intentionally (stores DayOfWeek.name())
    @Column(name = "day_of_week", nullable = false)
    private String dayOfWeek;

    @Column(name = "session_type", nullable = false)
    private String sessionType;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "batch_id")
    private Batch batch;

    @Column(name = "scheduling_level")
    private String schedulingLevel;

    @ManyToOne
    @JoinColumn(name = "time_slot_id")
    private TimeSlot timeSlot;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    public ScheduledClass() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Classroom getClassroom() { return classroom; }
    public void setClassroom(Classroom classroom) { this.classroom = classroom; }

    public CourseOffering getCourseOffering() { return courseOffering; }
    public void setCourseOffering(CourseOffering courseOffering) { this.courseOffering = courseOffering; }

    public Division getDivision() { return division; }
    public void setDivision(Division division) { this.division = division; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public String getSessionType() { return sessionType; }
    public void setSessionType(String sessionType) { this.sessionType = sessionType; }

    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }

    public Batch getBatch() { return batch; }
    public void setBatch(Batch batch) { this.batch = batch; }

    public String getSchedulingLevel() { return schedulingLevel; }
    public void setSchedulingLevel(String schedulingLevel) { this.schedulingLevel = schedulingLevel; }

    public TimeSlot getTimeSlot() { return timeSlot; }
    public void setTimeSlot(TimeSlot timeSlot) { this.timeSlot = timeSlot; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }
}

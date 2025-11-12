package com.rspc.timetable.optaplanner;

import com.rspc.timetable.entities.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.time.DayOfWeek;

@Entity
@Getter
@Setter
@PlanningEntity
@Table(name = "planned_classes")
public class PlannedClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @PlanningId
    private Long planningId;

    // Problem-fact relations (fixed)
    @ManyToOne private CourseOffering courseOffering;
    @ManyToOne private Division       division;
    @ManyToOne private Batch          batch;

    // Planning variables
    @ManyToOne
    @PlanningVariable(valueRangeProviderRefs = {"teacherRange"})
    private Teacher teacher;

    @ManyToOne
    @PlanningVariable(valueRangeProviderRefs = {"roomRange"})
    private Classroom classroom;

    @ManyToOne
    @PlanningVariable(valueRangeProviderRefs = {"slotRange"})
    private TimeSlot timeSlot;

    @Enumerated(EnumType.STRING)
    @PlanningVariable(valueRangeProviderRefs = {"dayRange"})
    private DayOfWeek day;

    // Additional properties used by constraints/optimizer
    @Enumerated(EnumType.STRING)
    private ScheduledClass.SessionType sessionType;

    // 1 for lecture/tutorial, 2 for double-slot labs, etc.
    private int lengthSlots = 1;

    // Aliases used by constraints/optimizer
    public Classroom getRoom() { return classroom; }
    public void setRoom(Classroom room) { this.classroom = room; }
    public TimeSlot getStartSlot() { return timeSlot; }
    public void setStartSlot(TimeSlot slot) { this.timeSlot = slot; }
    public CourseOffering getOffering() { return courseOffering; }
    public void setOffering(CourseOffering co) { this.courseOffering = co; }

    @PrePersist
    public void assignPlanningId() {
        if (planningId == null) planningId = (id != null) ? id : System.nanoTime();
    }
}

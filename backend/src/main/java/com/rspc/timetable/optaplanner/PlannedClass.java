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
@PlanningEntity(
    difficultyComparatorClass = PlannedClassDifficultyComparator.class
)
@Table(name = "planned_classes")
public class PlannedClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @PlanningId
    private Long planningId;

    @ManyToOne private CourseOffering courseOffering;
    @ManyToOne private Division       division;
    @ManyToOne private Batch          batch;

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

    @Enumerated(EnumType.STRING)
    private SessionType sessionType;

    private int lengthSlots = 1;

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

package com.rspc.timetable.optaplanner;

import com.rspc.timetable.entities.Classroom;
import com.rspc.timetable.entities.ScheduledClass;
import com.rspc.timetable.entities.TimeSlot;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class PlannedClass {

    @PlanningId
    private Long id;

    private ScheduledClass scheduledClass;

    @PlanningVariable(valueRangeProviderRefs = "timeSlotRange", nullable = true)
    private TimeSlot timeSlot;

    @PlanningVariable(valueRangeProviderRefs = "roomRange", nullable = true)
    private Classroom room;

    public PlannedClass() {}

    public PlannedClass(ScheduledClass scheduledClass) {
        this.scheduledClass = scheduledClass;
        if (scheduledClass != null) {
            this.id = scheduledClass.getId();
            this.timeSlot = scheduledClass.getTimeSlot();
            this.room = scheduledClass.getClassroom();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ScheduledClass getScheduledClass() {
        return scheduledClass;
    }

    public void setScheduledClass(ScheduledClass scheduledClass) {
        this.scheduledClass = scheduledClass;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    public Classroom getRoom() {
        return room;
    }

    public void setRoom(Classroom room) {
        this.room = room;
    }
}

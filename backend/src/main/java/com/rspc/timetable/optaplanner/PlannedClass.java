package com.rspc.timetable.optaplanner;

import com.rspc.timetable.entities.*;
import lombok.Getter;
import lombok.Setter;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@Getter
@Setter
@PlanningEntity
public class PlannedClass {

    private Long id; // ❗ No @PlanningId in OptaPlanner 9.x

    private CourseOffering offering;
    private Subject subject;
    private Division division;
    private Batch batch;
    private String sessionType;

    private Teacher teacher;

    // -------- Planning Variables --------
    @PlanningVariable(valueRangeProviderRefs = "dayRange")
    private Integer day;

    @PlanningVariable(valueRangeProviderRefs = "timeSlots")
    private TimeSlot timeSlot;

    @PlanningVariable(valueRangeProviderRefs = "rooms")
    private Classroom room;

    // Multi slot support
    private boolean multiSlot;
    private int slotCount;

    public PlannedClass() {}

    public PlannedClass(Long id,
                        CourseOffering offering,
                        Subject subject,
                        Division division,
                        Batch batch,
                        String sessionType,
                        boolean multiSlot,
                        int slotCount) {
        this.id = id;
        this.offering = offering;
        this.subject = subject;
        this.division = division;
        this.batch = batch;
        this.sessionType = sessionType;
        this.multiSlot = multiSlot;
        this.slotCount = slotCount;

        // Auto teacher assign
        if (offering != null) {
            this.teacher = offering.getTeacher();
        }
    }
}

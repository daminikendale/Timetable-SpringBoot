package com.rspc.timetable.optaplanner;

import com.rspc.timetable.entities.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@PlanningEntity
public class PlannedClass {

    private Long id;

    private CourseOffering offering;
    private Subject subject;
    private Division division;
    private Batch batch;
    private String sessionType;

    // The solver will choose a teacher. We provide two value ranges:
    // "eligibleTeacherRange" (per-class) and "teacherRange" (global from solution).
    @PlanningVariable(valueRangeProviderRefs = { "eligibleTeacherRange", "teacherRange" })
    private Teacher teacher;

    @PlanningVariable(valueRangeProviderRefs = "dayRange")
    private Integer day;

    @PlanningVariable(valueRangeProviderRefs = "timeSlots")
    private TimeSlot timeSlot;

    @PlanningVariable(valueRangeProviderRefs = "rooms")
    private Classroom room;

    // Multi slot support (for labs)
    private boolean multiSlot;
    private int slotCount = 1;

    // Per-class eligible teachers (populated at problem load time)
    private List<Teacher> eligibleTeachers = new ArrayList<>();

    @ValueRangeProvider(id = "eligibleTeacherRange")
    public List<Teacher> provideEligibleTeacherRange() {
        return eligibleTeachers == null ? new ArrayList<>() : eligibleTeachers;
    }

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
    }
}

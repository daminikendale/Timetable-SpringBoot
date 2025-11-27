package com.rspc.timetable.optaplanner;

import com.rspc.timetable.entities.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.lookup.PlanningId;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@PlanningEntity
public class PlannedClass {

    @PlanningId
    private Long id;

    private CourseOffering offering;
    private Subject subject;
    private Division division;
    private Batch batch;
    private String sessionType;

    private boolean fixed;
    private int hours;

    private List<Teacher> eligibleTeachers;

    @PlanningVariable(valueRangeProviderRefs = "roomRange")
    private Classroom room;

    @PlanningVariable(valueRangeProviderRefs = "timeRange")
    private TimeSlot timeSlot;

    @PlanningVariable(valueRangeProviderRefs = "teacherRange")
    private Teacher teacher;

    private Integer day;

    public PlannedClass(Long id, CourseOffering offering, Subject subject, Division division,
                        Batch batch, String sessionType, boolean fixed, int hours) {
        this.id = id;
        this.offering = offering;
        this.subject = subject;
        this.division = division;
        this.batch = batch;
        this.sessionType = sessionType;
        this.fixed = fixed;
        this.hours = hours;
    }
}

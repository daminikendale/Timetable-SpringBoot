package com.rspc.timetable.optaplanner;

import com.rspc.timetable.entities.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@PlanningSolution
public class TimetableSolution {

    @ValueRangeProvider(id = "roomRange")
    @ProblemFactCollectionProperty
    private List<Classroom> roomList;

    @ValueRangeProvider(id = "timeRange")
    @ProblemFactCollectionProperty
    private List<TimeSlot> timeSlotList;

    @ValueRangeProvider(id = "teacherRange")
    @ProblemFactCollectionProperty
    private List<Teacher> teacherList;

    @ProblemFactCollectionProperty
    private List<CourseOffering> offeringList;

    @PlanningEntityCollectionProperty
    private List<PlannedClass> plannedClassList;

    @PlanningScore
    private HardSoftScore score;
}

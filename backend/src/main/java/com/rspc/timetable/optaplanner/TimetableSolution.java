package com.rspc.timetable.optaplanner;

import com.rspc.timetable.entities.*;
import lombok.Getter;
import lombok.Setter;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.time.DayOfWeek;
import java.util.List;

@Getter @Setter
@PlanningSolution
public class TimetableSolution {

    // Problem facts
    @ProblemFactCollectionProperty private List<Division>  divisions;
    @ProblemFactCollectionProperty private List<Batch>     batches;
    @ProblemFactCollectionProperty private List<Teacher>   teachers;
    @ProblemFactCollectionProperty private List<Classroom> rooms;
    @ProblemFactCollectionProperty private List<TimeSlot>  slots;

    // Pinned rows (break/lunch)
    @ProblemFactCollectionProperty private List<ScheduledClass> pinnedRows;

    // Value ranges (primary ids)
    @ValueRangeProvider(id = "dayRange")
    @ProblemFactCollectionProperty private List<DayOfWeek> dayRange;

    @ValueRangeProvider(id = "slotRange")
    @ProblemFactCollectionProperty private List<TimeSlot> slotRange;

    @ValueRangeProvider(id = "roomRange")
    @ProblemFactCollectionProperty private List<Classroom> roomRange;

    @ValueRangeProvider(id = "teacherRange")
    @ProblemFactCollectionProperty private List<Teacher> teacherRange;

    // Aliases to satisfy older refs if any code still uses them
    @ValueRangeProvider(id = "classroomRange")
    public List<Classroom> getClassroomRange() { return rooms; }

    @ValueRangeProvider(id = "timeSlotRange")
    public List<TimeSlot> getTimeSlotRange() { return slots; }

    // Planning entities
    @PlanningEntityCollectionProperty
    private List<PlannedClass> classes;

    @PlanningScore
    private HardSoftScore score;
}

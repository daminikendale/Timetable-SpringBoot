package com.rspc.timetable.optaplanner;

import com.rspc.timetable.entities.*;
import lombok.Getter;
import lombok.Setter;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.*;

@Getter
@Setter
@PlanningSolution
public class TimetableSolution {

    /* ------------------- FACT RANGES ------------------- */

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "timeSlots")
    private List<TimeSlot> timeSlotList = new ArrayList<>();

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "rooms")
    private List<Classroom> roomList = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<Teacher> teacherList = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<Division> divisionList = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<Batch> batchList = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<CourseOffering> offeringList = new ArrayList<>();

    /* ------------------- DAY RANGE FIX 🔥 ------------------- */
    @ValueRangeProvider(id = "dayRange")
    @ProblemFactCollectionProperty
    private List<Integer> dayRangeList = defaultDayRange(5);

    public static List<Integer> defaultDayRange(int days) {
        List<Integer> r = new ArrayList<>();
        for (int i = 0; i < days; i++) r.add(i);
        return r;
    }

    /* ------------------- PLANNING ENTITIES ------------------- */

    @PlanningEntityCollectionProperty
    private List<PlannedClass> plannedClassList = new ArrayList<>();

    /* ------------------- SCORE ------------------- */

    @PlanningScore
    private HardSoftScore score;
}

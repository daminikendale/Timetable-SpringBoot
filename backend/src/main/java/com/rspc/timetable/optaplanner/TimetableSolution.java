package com.rspc.timetable.optaplanner;

import com.rspc.timetable.entities.*;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.solution.cloner.DeepPlanningClone;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.domain.solution.PlanningScore;

import java.util.List;

@PlanningSolution
@DeepPlanningClone
public class TimetableSolution {

    private List<Classroom> roomList;
    private List<TimeSlot> timeSlotList;
    private List<Teacher> teacherList;

    private List<PlannedClass> plannedClassList;

    private HardSoftScore score;

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "roomRange")
    public List<Classroom> getRoomList() { return roomList; }
    public void setRoomList(List<Classroom> roomList) { this.roomList = roomList; }

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "timeSlotRange")
    public List<TimeSlot> getTimeSlotList() { return timeSlotList; }
    public void setTimeSlotList(List<TimeSlot> timeSlotList) { this.timeSlotList = timeSlotList; }

    @ProblemFactCollectionProperty
    public List<Teacher> getTeacherList() { return teacherList; }
    public void setTeacherList(List<Teacher> teacherList) { this.teacherList = teacherList; }

    @PlanningEntityCollectionProperty
    public List<PlannedClass> getPlannedClassList() { return plannedClassList; }
    public void setPlannedClassList(List<PlannedClass> plannedClassList) { this.plannedClassList = plannedClassList; }

    @PlanningScore
    public HardSoftScore getScore() { return score; }
    public void setScore(HardSoftScore score) { this.score = score; }
}

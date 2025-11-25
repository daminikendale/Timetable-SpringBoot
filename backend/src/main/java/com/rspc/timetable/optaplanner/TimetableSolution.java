package com.rspc.timetable.optaplanner;

import com.rspc.timetable.entities.Classroom;
import com.rspc.timetable.entities.TimeSlot;
import org.optaplanner.core.api.domain.solution.*;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.List;

@PlanningSolution
public class TimetableSolution {

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "timeSlotRange")
    private List<TimeSlot> timeSlotList;

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "roomRange")
    private List<Classroom> roomList;

    @PlanningEntityCollectionProperty
    private List<PlannedClass> plannedClassList;

    @PlanningScore
    private HardSoftScore score;

    public TimetableSolution() {}

    public TimetableSolution(List<TimeSlot> timeSlotList,
                             List<Classroom> roomList,
                             List<PlannedClass> plannedClassList) {
        this.timeSlotList = timeSlotList;
        this.roomList = roomList;
        this.plannedClassList = plannedClassList;
    }

    public List<TimeSlot> getTimeSlotList() {
        return timeSlotList;
    }

    public void setTimeSlotList(List<TimeSlot> timeSlotList) {
        this.timeSlotList = timeSlotList;
    }

    public List<Classroom> getRoomList() {
        return roomList;
    }

    public void setRoomList(List<Classroom> roomList) {
        this.roomList = roomList;
    }

    public List<PlannedClass> getPlannedClassList() {
        return plannedClassList;
    }

    public void setPlannedClassList(List<PlannedClass> plannedClassList) {
        this.plannedClassList = plannedClassList;
    }

    public HardSoftScore getScore() {
        return score;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }
}

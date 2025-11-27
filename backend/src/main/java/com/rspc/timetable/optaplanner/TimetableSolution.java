package com.rspc.timetable.optaplanner;

import com.rspc.timetable.entities.Classroom;
import com.rspc.timetable.entities.TimeSlot;
import com.rspc.timetable.entities.Teacher;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.List;

@PlanningSolution
public class TimetableSolution {

    // value ranges
    @ValueRangeProvider(id = "roomRange")
    private List<Classroom> roomList;

    @ValueRangeProvider(id = "timeSlotRange")
    private List<TimeSlot> timeSlotList;

    // other lists for constraints
    private List<Teacher> teacherList;

    // Planning entities
    @PlanningEntityCollectionProperty
    private List<PlannedClass> plannedClassList;

    // Assignment lists (persisted JPA entities loaded into solution)
    private List<com.rspc.timetable.entities.LectureTeacherAssignment> lectureTeacherAssignments;
    private List<com.rspc.timetable.entities.BatchTeacherAssignment> batchTeacherAssignments;

    @PlanningScore
    private HardSoftScore score;

    // getters / setters
    public List<Classroom> getRoomList() { return roomList; }
    public void setRoomList(List<Classroom> roomList) { this.roomList = roomList; }

    public List<TimeSlot> getTimeSlotList() { return timeSlotList; }
    public void setTimeSlotList(List<TimeSlot> timeSlotList) { this.timeSlotList = timeSlotList; }

    public List<Teacher> getTeacherList() { return teacherList; }
    public void setTeacherList(List<Teacher> teacherList) { this.teacherList = teacherList; }

    public List<PlannedClass> getPlannedClassList() { return plannedClassList; }
    public void setPlannedClassList(List<PlannedClass> plannedClassList) { this.plannedClassList = plannedClassList; }

    public List<com.rspc.timetable.entities.LectureTeacherAssignment> getLectureTeacherAssignments() { return lectureTeacherAssignments; }
    public void setLectureTeacherAssignments(List<com.rspc.timetable.entities.LectureTeacherAssignment> lectureTeacherAssignments) { this.lectureTeacherAssignments = lectureTeacherAssignments; }

    public List<com.rspc.timetable.entities.BatchTeacherAssignment> getBatchTeacherAssignments() { return batchTeacherAssignments; }
    public void setBatchTeacherAssignments(List<com.rspc.timetable.entities.BatchTeacherAssignment> batchTeacherAssignments) { this.batchTeacherAssignments = batchTeacherAssignments; }

    public HardSoftScore getScore() { return score; }
    public void setScore(HardSoftScore score) { this.score = score; }
}

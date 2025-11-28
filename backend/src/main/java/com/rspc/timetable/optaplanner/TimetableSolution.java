package com.rspc.timetable.optaplanner;

import com.rspc.timetable.entities.ScheduledClass;
import com.rspc.timetable.entities.TimeSlot;
import com.rspc.timetable.entities.Teacher;
import com.rspc.timetable.entities.Classroom;
import com.rspc.timetable.entities.Division;
import com.rspc.timetable.entities.Subject;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.List;

@PlanningSolution
public class TimetableSolution {

    // ---------------------------------------
    // ✔ 1. Planning entities
    // ---------------------------------------
    @PlanningEntityCollectionProperty
    private List<ScheduledClass> scheduledClasses;

    // ---------------------------------------
    // ✔ 2. Problem facts + ValueRanges
    // ---------------------------------------
    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "timeSlotRange")
    private List<TimeSlot> timeSlots;

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "teacherRange")
    private List<Teacher> teachers;

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "roomRange")
    private List<Classroom> classrooms;

    @ProblemFactCollectionProperty
    private List<Division> divisions;

    @ProblemFactCollectionProperty
    private List<Subject> subjects;

    // ---------------------------------------
    // ✔ 3. Score
    // ---------------------------------------
    @PlanningScore
    private HardSoftScore score;

    // ---------------------------------------
    // ✔ Getters & Setters (correct)
    // ---------------------------------------

    public List<ScheduledClass> getScheduledClasses() {
        return scheduledClasses;
    }

    public void setScheduledClasses(List<ScheduledClass> scheduledClasses) {
        this.scheduledClasses = scheduledClasses;
    }

    public List<TimeSlot> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<TimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<Teacher> teachers) {
        this.teachers = teachers;
    }

    public List<Classroom> getClassrooms() {
        return classrooms;
    }

    public void setClassrooms(List<Classroom> classrooms) {
        this.classrooms = classrooms;
    }

    public List<Division> getDivisions() {
        return divisions;
    }

    public void setDivisions(List<Division> divisions) {
        this.divisions = divisions;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }

    public HardSoftScore getScore() {
        return score;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }
}

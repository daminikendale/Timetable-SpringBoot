package com.rspc.timetable.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "teacher_subject_allocation")
public class TeacherSubjectAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "teacher_id")
    private Long teacherId;

    @Column(name = "year_id")
    private Long yearId;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public Long getYearId() { return yearId; }
    public void setYearId(Long yearId) { this.yearId = yearId; }
}

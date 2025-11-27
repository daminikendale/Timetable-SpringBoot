package com.rspc.timetable.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "lecture_teacher_assignments")
public class LectureTeacherAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // division_id
    @ManyToOne
    @JoinColumn(name = "division_id", nullable = false)
    private Division division;

    // subject_id
    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    // teacher_id
    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    public LectureTeacherAssignment() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Division getDivision() { return division; }
    public void setDivision(Division division) { this.division = division; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }
}

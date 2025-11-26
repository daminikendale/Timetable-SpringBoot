package com.rspc.timetable.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "course_offerings")
public class CourseOffering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Subject being taught
    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    // Department
    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    // Year
    @ManyToOne
    @JoinColumn(name = "year_id", nullable = false)
    private Year year;

    // Semester
    @ManyToOne
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    // ⭐ REQUIRED FOR OPTAPLANNER (Fixes your error)
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    // Weekly structure
    @Column(nullable = false)
    private int lecPerWeek;

    @Column(nullable = false)
    private int tutPerWeek;

    @Column(nullable = false)
    private int labPerWeek;

    @Column(nullable = false)
    private int weeklyHours;
}

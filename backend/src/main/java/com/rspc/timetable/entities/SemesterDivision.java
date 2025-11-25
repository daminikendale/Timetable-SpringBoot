package com.rspc.timetable.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "semester_divisions")
public class SemesterDivision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "semester_id")
    private Semester semester;

    @ManyToOne(optional = false)
    @JoinColumn(name = "division_id")
    private Division division;

    public SemesterDivision() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
    }
}

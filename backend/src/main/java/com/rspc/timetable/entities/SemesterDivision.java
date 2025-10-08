package com.rspc.timetable.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "semester_divisions")
@NoArgsConstructor // JPA requires a no-argument constructor
@AllArgsConstructor // Creates the constructor we need: new SemesterDivision(semester, division)
public class SemesterDivision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "division_id", nullable = false)
    private Division division;

    // A convenience constructor without the 'id' for easy creation
    public SemesterDivision(Semester semester, Division division) {
        this.semester = semester;
        this.division = division;
    }
}

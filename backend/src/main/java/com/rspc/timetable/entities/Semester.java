package com.rspc.timetable.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "semesters")
public class Semester {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The 'name' field should be removed.

    @Column(nullable = false)
    private int semesterNumber; // e.g., 1, 3, 5

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "year_id", nullable = false)
    private Year year;
}

package com.rspc.timetable.entities;

import jakarta.persistence.*;

@Entity
public class Allocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Teacher teacher;

    @ManyToOne
    private Subject subject;

    @ManyToOne
    private Division division;

    @ManyToOne
    private Classroom classroom;

    private String className; // e.g. "Room 11", "Comp-1 Lab"
}

package com.rspc.timetable.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "divisions")
public class Division {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "division_name", unique = true, nullable = false)
    private String divisionName; // e.g., "A", "B"
}

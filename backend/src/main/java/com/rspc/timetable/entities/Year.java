package com.rspc.timetable.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "years")
public class Year {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Must match your DTO field yearName
    @Column(nullable = false, unique = true)
    private String yearName;

    // Optional: convenience constructor
    public Year() {}

    public Year(String yearName) {
        this.yearName = yearName;
    }
}

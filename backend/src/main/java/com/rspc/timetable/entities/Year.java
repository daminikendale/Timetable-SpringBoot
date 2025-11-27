package com.rspc.timetable.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "years")
public class Year {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "year_name", nullable = false, unique = true)
    private String yearName;

    // Self reference if year_id exists (your schema allows it)
    @ManyToOne
    @JoinColumn(name = "year_id")  // nullable in DB
    private Year parentYear;

    public Year() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getYearName() { return yearName; }
    public void setYearName(String yearName) { this.yearName = yearName; }

    public Year getParentYear() { return parentYear; }
    public void setParentYear(Year parentYear) { this.parentYear = parentYear; }
}

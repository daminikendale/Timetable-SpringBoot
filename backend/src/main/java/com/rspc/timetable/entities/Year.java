package com.rspc.timetable.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "years")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Year {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int yearNumber;  // 1, 2, 3, or 4

    @OneToMany(mappedBy = "year", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Semester> semesters;

    public Year() {}

    public Year(int yearNumber, List<Semester> semesters) {
        this.yearNumber = yearNumber;
        this.semesters = semesters;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getYearNumber() { return yearNumber; }
    public void setYearNumber(int yearNumber) { this.yearNumber = yearNumber; }

    public List<Semester> getSemesters() { return semesters; }
    public void setSemesters(List<Semester> semesters) { this.semesters = semesters; }
}

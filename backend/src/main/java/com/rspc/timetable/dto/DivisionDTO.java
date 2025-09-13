package com.rspc.timetable.dto;

public class DivisionDTO {
    private Long id;
    private String name;
    private int yearNumber;
    private int semesterNumber; // added

    public DivisionDTO(Long id, String name, int yearNumber, int semesterNumber) {
        this.id = id;
        this.name = name;
        this.yearNumber = yearNumber;
        this.semesterNumber = semesterNumber;
    }

    // Getters and setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getYearNumber() { return yearNumber; }
    public void setYearNumber(int yearNumber) { this.yearNumber = yearNumber; }

    public int getSemesterNumber() { return semesterNumber; }
    public void setSemesterNumber(int semesterNumber) { this.semesterNumber = semesterNumber; }
}

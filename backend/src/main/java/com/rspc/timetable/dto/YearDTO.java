package com.rspc.timetable.dto;

public class YearDTO {
    private Long id;
    private int yearNumber;

    public YearDTO(Long id, int yearNumber) {
        this.id = id;
        this.yearNumber = yearNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getYearNumber() {
        return yearNumber;
    }

    public void setYearNumber(int yearNumber) {
        this.yearNumber = yearNumber;
    }
}

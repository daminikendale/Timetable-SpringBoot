package com.rspc.timetable.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TeacherDTO {
    private Long id;
    private String name;
    private String email;

    @JsonProperty("employee_id")  // maps JSON "employee_id" -> Java "employeeId"
    private String employeeId;

    public TeacherDTO() {}

    public TeacherDTO(Long id, String name, String email, String employeeId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.employeeId = employeeId;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
}

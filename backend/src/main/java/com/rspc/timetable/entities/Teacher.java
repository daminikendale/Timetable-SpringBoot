package com.rspc.timetable.entities;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "teachers")
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "employee_id")
    private String employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    // FINAL & CORRECT WAY TO MODEL UNAVAILABILITY
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "teacher_unavailable_timeslots",
        joinColumns = @JoinColumn(name = "teacher_id"),
        inverseJoinColumns = @JoinColumn(name = "timeslot_id")
    )
    private Set<TimeSlot> unavailableTimeSlots = new HashSet<>();

    // ---------------------- Getters & Setters ----------------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public Set<TimeSlot> getUnavailableTimeSlots() { return unavailableTimeSlots; }

    public void setUnavailableTimeSlots(Set<TimeSlot> unavailableTimeSlots) {
        this.unavailableTimeSlots = 
            (unavailableTimeSlots == null) ? new HashSet<>() : unavailableTimeSlots;
    }
}

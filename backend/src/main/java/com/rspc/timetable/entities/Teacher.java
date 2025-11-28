package com.rspc.timetable.entities;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "teachers")
public class Teacher {

    @Id
    private Long id;

    private String name;

    private String email;

    @Column(name = "employee_id")
    private String employeeId;

    // Department relation
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    // Subjects teacher can teach
    @ManyToMany
    @JoinTable(
        name = "teacher_subject_allocation",
        joinColumns = @JoinColumn(name = "teacher_id"),
        inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private Set<Subject> subjects;

    // Unavailable timeslots
    @ManyToMany
    @JoinTable(
        name = "teacher_unavailable_timeslots",
        joinColumns = @JoinColumn(name = "teacher_id"),
        inverseJoinColumns = @JoinColumn(name = "timeslot_id")
    )
    private Set<TimeSlot> unavailableTimeSlots;

    // --- Getters and Setters ---

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

    public Set<Subject> getSubjects() { return subjects; }
    public void setSubjects(Set<Subject> subjects) { this.subjects = subjects; }

    public Set<TimeSlot> getUnavailableTimeSlots() { return unavailableTimeSlots; }
    public void setUnavailableTimeSlots(Set<TimeSlot> unavailableTimeSlots) {
        this.unavailableTimeSlots = unavailableTimeSlots;
    }
}

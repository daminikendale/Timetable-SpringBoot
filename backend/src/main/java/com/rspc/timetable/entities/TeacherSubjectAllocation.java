package com.rspc.timetable.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "teacher_subject_allocation")
public class TeacherSubjectAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "teacher_id")
    private Long teacherId;

    @Column(name = "year_id")
    private Long yearId;

    // NEW FIELDS
    @Column(name = "semester_id")
    private Long semesterId;

    // In TeacherSubjectAllocation.java
@Enumerated(EnumType.STRING)
@Column(name = "role", nullable = false)
private Role role = Role.THEORY;



    @Column(name = "priority")
    private Integer priority;          // lower means more preferred

    @Column(name = "capacity_hours")
    private Integer capacityHours;     // optional weekly capacity

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(name = "notes")
    private String notes;

    public enum Role { THEORY, LAB }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public Long getYearId() { return yearId; }
    public void setYearId(Long yearId) { this.yearId = yearId; }

    public Long getSemesterId() { return semesterId; }
    public void setSemesterId(Long semesterId) { this.semesterId = semesterId; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public Integer getCapacityHours() { return capacityHours; }
    public void setCapacityHours(Integer capacityHours) { this.capacityHours = capacityHours; }

    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }

    public LocalDate getValidTo() { return validTo; }
    public void setValidTo(LocalDate validTo) { this.validTo = validTo; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}

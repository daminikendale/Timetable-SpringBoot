package com.rspc.timetable.entities;

import jakarta.persistence.*;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(name = "teachers")
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;
    
    @Column(name = "employee_id", unique = true)
    private String employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "teacher_unavailability", joinColumns = @JoinColumn(name = "teacher_id"))
    @Column(name = "timeslot_id")
    private List<Long> unavailableTimeSlots;
}

package com.rspc.timetable.entities;

import jakarta.persistence.*;

@Entity
public class Classroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;  // e.g., Room 11, Project lab

    private int capacity;
    @Enumerated(EnumType.STRING)
    private ClassroomType type;  // NORMAL or LAB

    public Classroom() {}

    public Classroom(String name, ClassroomType type) {
        this.name = name;
        this.type = type;
        this.capacity = capacity;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public ClassroomType getType() { return type; }
    public void setType(ClassroomType type) { this.type = type; }
}

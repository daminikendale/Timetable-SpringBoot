package com.rspc.timetable.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "classrooms")
public class Classroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    private ClassroomType type;

    public ClassroomType getType() { return type; }
    public void setType(ClassroomType type) { this.type = type; }
}

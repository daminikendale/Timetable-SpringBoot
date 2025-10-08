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

    @Column(unique = true, nullable = false)
    private String roomNumber;

    @Column(nullable = false)
    private int capacity;

    /**
     * Defines the type of the classroom. This is crucial for the timetable generator
     * to correctly assign LAB sessions to LAB rooms, THEORY to LECTURE_HALLS, etc.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClassroomType type;

    // The associated enum for the classroom type
    public enum ClassroomType {
        LECTURE_HALL,
        LAB,
        TUTORIAL_ROOM
    }
}

package com.rspc.timetable.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "classrooms")
public class Classroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // the canonical field used in DB and UI
    @Column(nullable = false, unique = true)
    private String name;

    private int capacity;

    @Column(length = 20)
    private String type;

    // Backward-compat: allow legacy code using roomNumber
    @Deprecated
    public String getRoomNumber() {
        return this.name;
    }

    @Deprecated
    public void setRoomNumber(String roomNumber) {
        this.name = roomNumber;
    }

    @Override
    public String toString() {
        return name + " (" + type + ", cap=" + capacity + ")";
    }
}

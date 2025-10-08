package com.rspc.timetable.dto;

import com.rspc.timetable.entities.Classroom;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomDTO {
    private Long id;
    private String roomNumber;
    private int capacity;
    private String type; // Using String for simplicity in DTOs

    // Convenience constructor to map from the entity
    public ClassroomDTO(Classroom classroom) {
        this.id = classroom.getId();
        this.roomNumber = classroom.getRoomNumber();
        this.capacity = classroom.getCapacity();
        this.type = classroom.getType().name();
    }
}

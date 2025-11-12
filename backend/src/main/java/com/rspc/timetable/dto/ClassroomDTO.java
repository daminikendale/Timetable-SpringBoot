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
    private String roomNumber; // maps to Classroom.name
    private Integer capacity;
    private String type;

    public ClassroomDTO(Classroom classroom) {
        if (classroom != null) {
            this.id = classroom.getId();
            this.roomNumber = classroom.getName(); // map from 'name'
            this.capacity = classroom.getCapacity();
            this.type = classroom.getType();
        }
    }

    public Classroom toEntity() {
        Classroom c = new Classroom();
        c.setId(this.id);
        c.setName(this.roomNumber); // map to 'name'
        c.setCapacity(this.capacity != null ? this.capacity : 0);
        c.setType(this.type);
        return c;
    }
}

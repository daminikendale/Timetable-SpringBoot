package com.rspc.timetable.dto;

import com.rspc.timetable.entities.Classroom;
import com.rspc.timetable.entities.ClassroomType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomDTO {
    private Long id;
    private String roomNumber;
    private Integer capacity;
    private String type; // String for front-end but maps to ClassroomType enum

    public ClassroomDTO(Classroom classroom) {
        if (classroom != null) {
            this.id = classroom.getId();
            this.roomNumber = classroom.getName();
            this.capacity = classroom.getCapacity();
            this.type = classroom.getType() != null ? classroom.getType().name() : null;
        }
    }

    public Classroom toEntity() {
        Classroom c = new Classroom();
        c.setId(this.id);
        c.setName(this.roomNumber);
        c.setCapacity(this.capacity != null ? this.capacity : 0);
        if (this.type != null) {
            c.setType(ClassroomType.valueOf(this.type.toUpperCase()));
        } else {
            c.setType(null);
        }
        return c;
    }
}

package com.rspc.timetable.dto;

import com.rspc.timetable.entities.Subject;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDTO {

    private Long id;
    private String name;
    private String code;

    // REGULAR, HONORS, ELECTIVE
    private Subject.SubjectCategory category;

    // THEORY, LAB, TUTORIAL
    private Subject.SubjectType type;

    private Long semesterId;

    // Entity → DTO
    public SubjectDTO(Subject subject) {
        this.id = subject.getId();
        this.name = subject.getName();
        this.code = subject.getCode();
        this.category = subject.getCategory();
        this.type = subject.getType();        // <── FIXED
        this.semesterId = subject.getSemester() != null ? subject.getSemester().getId() : null;
    }
}

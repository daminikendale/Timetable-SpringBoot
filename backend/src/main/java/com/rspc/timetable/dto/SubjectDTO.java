package com.rspc.timetable.dto;

import com.rspc.timetable.entities.Subject;
import com.rspc.timetable.entities.SubjectCategory;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDTO {
    private Long id;
    private String code;
    private String name;
    private Long semesterId;
    private SubjectCategory category;
    private String subjectType;

    public SubjectDTO(Subject subject) {
        this.id = subject.getId();
        this.code = subject.getCode();
        this.name = subject.getName();
        this.semesterId = subject.getSemester() != null ? subject.getSemester().getId() : null;
        this.category = subject.getCategory();
        this.subjectType = subject.getSubjectType() != null ? subject.getSubjectType().name() : null;
    }
}

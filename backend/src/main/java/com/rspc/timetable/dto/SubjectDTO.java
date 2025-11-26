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
    private Subject.SubjectCategory category;
    private Subject.SubjectType subjectType;
    private Long semesterId;

    public SubjectDTO(Subject subject) {
        this.id = subject.getId();
        this.name = subject.getName();
        this.code = subject.getCode();
        this.category = subject.getCategory();
        this.subjectType = subject.getSubjectType();
        this.semesterId = subject.getSemester() != null ? subject.getSemester().getId() : null;
    }
}

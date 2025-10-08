package com.rspc.timetable.dto;

import com.rspc.timetable.entities.Subject;
import com.rspc.timetable.entities.Subject.SubjectCategory;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubjectDTO {

    private Long id;
    private String code;
    private String name;
    private int priority;
    private SubjectCategory category;
    private Long semesterId;
    private String semesterName;

    public SubjectDTO(Subject subject) {
        this.id = subject.getId();
        this.code = subject.getCode();
        this.name = subject.getName();
        this.priority = subject.getPriority();
        this.category = subject.getCategory();
        
        if (subject.getSemester() != null) {
            this.semesterId = subject.getSemester().getId();
            if (subject.getSemester().getYear() != null) {
                this.semesterName = "Sem " + subject.getSemester().getSemesterNumber() + " - " + subject.getSemester().getYear().getYearName();
            } else {
                this.semesterName = "Sem " + subject.getSemester().getSemesterNumber();
            }
        }
    }
}

package com.rspc.timetable.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherSubjectAllocationDTO {

    // This field is essential for the setId() method to exist
    private Long id; 

    private Long teacherId;
    private Long subjectId;

     private int priority;

}

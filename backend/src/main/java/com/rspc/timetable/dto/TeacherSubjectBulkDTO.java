package com.rspc.timetable.dto;

import lombok.Data;
import java.util.List;

@Data
public class TeacherSubjectBulkDTO {
    private Long subjectId;
    private List<Long> teacherIds;
     
}

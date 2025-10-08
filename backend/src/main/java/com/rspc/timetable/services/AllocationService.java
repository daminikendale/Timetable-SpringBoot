package com.rspc.timetable.services;

import com.rspc.timetable.dto.TeacherSubjectAllocationDTO;
import java.util.List;
import java.util.Map;

public interface AllocationService {

    TeacherSubjectAllocationDTO createAllocation(TeacherSubjectAllocationDTO allocationDTO);

    List<TeacherSubjectAllocationDTO> createBulkAllocations(List<TeacherSubjectAllocationDTO> allocationDTOs);
    
    List<TeacherSubjectAllocationDTO> createAllocationsFromMap(Map<Long, List<Long>> subjectToTeachersMap);

    List<TeacherSubjectAllocationDTO> getAllAllocations();

    // Add these missing method declarations
    List<TeacherSubjectAllocationDTO> getAllocationsByTeacher(Long teacherId);

    List<TeacherSubjectAllocationDTO> getAllocationsBySubject(Long subjectId);
}

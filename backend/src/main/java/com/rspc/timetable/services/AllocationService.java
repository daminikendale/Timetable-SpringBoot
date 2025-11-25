package com.rspc.timetable.services;

import com.rspc.timetable.dto.TeacherSubjectAllocationDTO;
import java.util.List;
import java.util.Map;

/**
 * Allocation operations for mapping teachers to subjects.
 */
public interface AllocationService {

    /**
     * Create a single teacher-subject allocation.
     */
    TeacherSubjectAllocationDTO createAllocation(TeacherSubjectAllocationDTO allocationDTO);

    /**
     * Create multiple allocations in one call.
     */
    List<TeacherSubjectAllocationDTO> createBulkAllocations(List<TeacherSubjectAllocationDTO> allocationDTOs);

    /**
     * Create allocations from a map of subjectId -> list of teacherIds.
     */
    List<TeacherSubjectAllocationDTO> createAllocationsFromMap(Map<Long, List<Long>> subjectToTeachersMap);

    /**
     * Fetch all allocations.
     */
    List<TeacherSubjectAllocationDTO> getAllAllocations();

    /**
     * Fetch allocations for a specific teacher.
     */
    List<TeacherSubjectAllocationDTO> getAllocationsByTeacher(Long teacherId);

    /**
     * Fetch allocations for a specific subject.
     */
    List<TeacherSubjectAllocationDTO> getAllocationsBySubject(Long subjectId);
}

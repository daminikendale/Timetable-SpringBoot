package com.rspc.timetable.services;

import com.rspc.timetable.dto.CourseOfferingDTO;
import java.util.List;

public interface CourseOfferingService {

    CourseOfferingDTO createCourseOffering(CourseOfferingDTO dto);

    CourseOfferingDTO getOfferingById(Long id);

    List<CourseOfferingDTO> getOfferingsBySubjectAndSemesters(Long subjectId, List<Integer> semesterNumbers);

    List<CourseOfferingDTO> getOfferingsBySemesterNumbers(List<Integer> semesterNumbers);

    List<CourseOfferingDTO> getAllOfferings();

    // Add these to match the controller
    List<CourseOfferingDTO> createBulkCourseOfferings(List<CourseOfferingDTO> offerings);

    void deleteOffering(Long id);
}

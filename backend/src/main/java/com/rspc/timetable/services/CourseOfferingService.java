package com.rspc.timetable.services;

import com.rspc.timetable.dto.CourseOfferingDTO;
import java.util.List;

public interface CourseOfferingService {

    CourseOfferingDTO createCourseOffering(CourseOfferingDTO dto);

    List<CourseOfferingDTO> createBulkCourseOfferings(List<CourseOfferingDTO> dtos);

    List<CourseOfferingDTO> getAllOfferings();

    CourseOfferingDTO getOfferingById(Long id);

    void deleteOffering(Long id);

    List<CourseOfferingDTO> getOfferingsBySemesterNumbers(List<Integer> semesterNumbers);

    List<CourseOfferingDTO> getOfferingsBySubjectAndSemesters(Long subjectId, List<Integer> semesterNumbers);
}

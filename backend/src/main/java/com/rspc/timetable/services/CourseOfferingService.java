package com.rspc.timetable.services;

import com.rspc.timetable.dto.CourseOfferingDTO;
import java.util.List;

/**
 * Interface for managing CourseOfferings.
 * Defines the contract for creating, retrieving, and deleting course offerings.
 */
public interface CourseOfferingService {

    /**
     * Creates a single course offering from a DTO.
     */
    CourseOfferingDTO createCourseOffering(CourseOfferingDTO dto);

    /**
     * Creates multiple course offerings from a list of DTOs.
     * This is the method that must be added to your interface.
     */
    List<CourseOfferingDTO> createBulkCourseOfferings(List<CourseOfferingDTO> dtos);

    /**
     * Retrieves all course offerings.
     */
    List<CourseOfferingDTO> getAllOfferings();

    /**
     * Retrieves a single course offering by its ID.
     */
    CourseOfferingDTO getOfferingById(Long id);
    
    /**
     * Deletes a course offering by its ID.
     */
    void deleteOffering(Long id);
}

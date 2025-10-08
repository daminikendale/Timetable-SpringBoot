package com.rspc.timetable.dto;

import java.util.List;

// Using a record for a concise, immutable DTO
public record TeacherDTO(
    Long id,
    String name,
    String email,
    String employeeId,
    Long departmentId, // Use the ID for the department relationship
    List<Long> unavailableTimeSlots // List of TimeSlot IDs
) {}

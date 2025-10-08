package com.rspc.timetable.services;

import com.rspc.timetable.dto.TeacherDTO;
import com.rspc.timetable.entities.Department;
import com.rspc.timetable.entities.Teacher;
import com.rspc.timetable.repositories.DepartmentRepository;
import com.rspc.timetable.repositories.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final DepartmentRepository departmentRepository; // Inject to handle the relationship

    public TeacherService(TeacherRepository teacherRepository, DepartmentRepository departmentRepository) {
        this.teacherRepository = teacherRepository;
        this.departmentRepository = departmentRepository;
    }

    // --- DTO to Entity Mapping (Helper Method) ---
    private Teacher convertToEntity(TeacherDTO dto) {
        Teacher teacher = new Teacher();
        teacher.setId(dto.id());
        teacher.setName(dto.name());
        teacher.setEmail(dto.email());
        teacher.setEmployeeId(dto.employeeId()); // Assuming employeeId is now in the Teacher entity
        
        // Handle the department relationship
        if (dto.departmentId() != null) {
            Department department = departmentRepository.findById(dto.departmentId())
                .orElseThrow(() -> new EntityNotFoundException("Department not found with ID: " + dto.departmentId()));
            teacher.setDepartment(department);
        }

        teacher.setUnavailableTimeSlots(dto.unavailableTimeSlots());
        
        return teacher;
    }

    // --- Entity to DTO Mapping (Helper Method) ---
    private TeacherDTO convertToDTO(Teacher teacher) {
        return new TeacherDTO(
            teacher.getId(),
            teacher.getName(),
            teacher.getEmail(),
            teacher.getEmployeeId(),
            teacher.getDepartment() != null ? teacher.getDepartment().getId() : null,
            teacher.getUnavailableTimeSlots()
        );
    }
    
    // --- Service Methods Using DTOs ---

    @Transactional(readOnly = true)
    public List<TeacherDTO> getAllTeachers() {
        return teacherRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TeacherDTO getTeacherById(Long id) {
        return teacherRepository.findById(id)
            .map(this::convertToDTO)
            .orElseThrow(() -> new EntityNotFoundException("Teacher not found with ID: " + id));
    }

    @Transactional
    public TeacherDTO createTeacher(TeacherDTO teacherDTO) {
        Teacher teacher = convertToEntity(teacherDTO);
        Teacher savedTeacher = teacherRepository.save(teacher);
        return convertToDTO(savedTeacher);
    }

    @Transactional
    public TeacherDTO updateTeacher(Long id, TeacherDTO teacherDTO) {
        // Ensure the teacher exists
        if (!teacherRepository.existsById(id)) {
            throw new EntityNotFoundException("Teacher not found with ID: " + id);
        }
        Teacher teacherToUpdate = convertToEntity(teacherDTO);
        teacherToUpdate.setId(id); // Ensure we are updating the correct entity
        Teacher updatedTeacher = teacherRepository.save(teacherToUpdate);
        return convertToDTO(updatedTeacher);
    }

    @Transactional
    public void deleteTeacher(Long id) {
        if (!teacherRepository.existsById(id)) {
            throw new EntityNotFoundException("Teacher not found with ID: " + id);
        }
        teacherRepository.deleteById(id);
    }

    @Transactional
    public List<TeacherDTO> saveAllTeachersFromDTOs(List<TeacherDTO> teacherDTOs) {
        List<Teacher> teachers = teacherDTOs.stream()
            .map(this::convertToEntity)
            .collect(Collectors.toList());
        
        List<Teacher> savedTeachers = teacherRepository.saveAll(teachers);
        
        return savedTeachers.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
}

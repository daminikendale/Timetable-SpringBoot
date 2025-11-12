package com.rspc.timetable.services;

import com.rspc.timetable.dto.TeacherDTO;
import com.rspc.timetable.entities.Department;
import com.rspc.timetable.entities.Teacher;
import com.rspc.timetable.repositories.DepartmentRepository;
import com.rspc.timetable.repositories.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final DepartmentRepository departmentRepository;

    public TeacherService(TeacherRepository teacherRepository,
                          DepartmentRepository departmentRepository) {
        this.teacherRepository = teacherRepository;
        this.departmentRepository = departmentRepository;
    }

    // ---- Mapping helpers ----
    private Teacher convertToEntity(TeacherDTO dto) {
        Teacher teacher = new Teacher();
        teacher.setId(dto.id());
        teacher.setName(dto.name());
        teacher.setEmail(dto.email());
        teacher.setEmployeeId(dto.employeeId());
        if (dto.departmentId() != null) {
            Department dept = departmentRepository.findById(dto.departmentId())
                .orElseThrow(() -> new EntityNotFoundException("Department not found with ID: " + dto.departmentId()));
            teacher.setDepartment(dept);
        }
        if (dto.unavailableTimeSlots() != null) {
            teacher.setUnavailableTimeSlots(dto.unavailableTimeSlots());
        }
        return teacher;
    }

    private TeacherDTO convertToDTO(Teacher teacher) {
        Long deptId = teacher.getDepartment() != null ? teacher.getDepartment().getId() : null;
        List slots = teacher.getUnavailableTimeSlots() != null
            ? teacher.getUnavailableTimeSlots()
            : Collections.emptyList();
        return new TeacherDTO(
            teacher.getId(),
            teacher.getName(),
            teacher.getEmail(),
            teacher.getEmployeeId(),
            deptId,
            slots
        );
    }

    // ---- CRUD using DTOs ----
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
        Teacher saved = teacherRepository.save(teacher);
        return convertToDTO(saved);
    }

    @Transactional
    public TeacherDTO updateTeacher(Long id, TeacherDTO teacherDTO) {
        Teacher existing = teacherRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Teacher not found with ID: " + id));

        if (teacherDTO.name() != null) existing.setName(teacherDTO.name());
        if (teacherDTO.email() != null) existing.setEmail(teacherDTO.email());
        if (teacherDTO.employeeId() != null) existing.setEmployeeId(teacherDTO.employeeId());
        if (teacherDTO.departmentId() != null) {
            Department dept = departmentRepository.findById(teacherDTO.departmentId())
                .orElseThrow(() -> new EntityNotFoundException("Department not found with ID: " + teacherDTO.departmentId()));
            existing.setDepartment(dept);
        }
        if (teacherDTO.unavailableTimeSlots() != null) {
            existing.setUnavailableTimeSlots(teacherDTO.unavailableTimeSlots());
        }

        Teacher updated = teacherRepository.save(existing);
        return convertToDTO(updated);
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
        List<Teacher> toSave = teacherDTOs.stream()
            .map(this::convertToEntity)
            .collect(Collectors.toList());
        List<Teacher> saved = teacherRepository.saveAll(toSave);
        return saved.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
}

package com.rspc.timetable.services.implementation;

import com.rspc.timetable.dto.TeacherSubjectAllocationDTO;
import com.rspc.timetable.entities.Subject;
import com.rspc.timetable.entities.Teacher;
import com.rspc.timetable.entities.TeacherSubjectAllocation;
import com.rspc.timetable.repositories.SubjectRepository;
import com.rspc.timetable.repositories.TeacherRepository;
import com.rspc.timetable.repositories.TeacherSubjectAllocationRepository;
import com.rspc.timetable.services.AllocationService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AllocationServiceImpl implements AllocationService {

    private final TeacherSubjectAllocationRepository allocationRepository;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;

    public AllocationServiceImpl(
            TeacherSubjectAllocationRepository allocationRepository,
            @Lazy TeacherRepository teacherRepository,
            @Lazy SubjectRepository subjectRepository) {
        this.allocationRepository = allocationRepository;
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
    }

    @Override
    @Transactional
    public TeacherSubjectAllocationDTO createAllocation(TeacherSubjectAllocationDTO allocationDTO) {
        Teacher teacher = teacherRepository.findById(allocationDTO.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found with ID: " + allocationDTO.getTeacherId()));
        Subject subject = subjectRepository.findById(allocationDTO.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found with ID: " + allocationDTO.getSubjectId()));

        TeacherSubjectAllocation allocation = new TeacherSubjectAllocation();
        allocation.setTeacher(teacher);
        allocation.setSubject(subject);

        TeacherSubjectAllocation savedAllocation = allocationRepository.save(allocation);
        return mapToDTO(savedAllocation);
    }

    @Override
    @Transactional
    public List<TeacherSubjectAllocationDTO> createBulkAllocations(List<TeacherSubjectAllocationDTO> allocationDTOs) {
        List<TeacherSubjectAllocation> allocationsToSave = new ArrayList<>();
        for (TeacherSubjectAllocationDTO dto : allocationDTOs) {
            Teacher teacher = teacherRepository.findById(dto.getTeacherId())
                    .orElseThrow(() -> new RuntimeException("Teacher not found with ID: " + dto.getTeacherId()));
            Subject subject = subjectRepository.findById(dto.getSubjectId())
                    .orElseThrow(() -> new RuntimeException("Subject not found with ID: " + dto.getSubjectId()));
            
            TeacherSubjectAllocation allocation = new TeacherSubjectAllocation();
            allocation.setTeacher(teacher);
            allocation.setSubject(subject);
            allocationsToSave.add(allocation);
        }
        
        List<TeacherSubjectAllocation> savedAllocations = allocationRepository.saveAll(allocationsToSave);
        return savedAllocations.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<TeacherSubjectAllocationDTO> createAllocationsFromMap(Map<Long, List<Long>> subjectToTeachersMap) {
        List<TeacherSubjectAllocation> allocationsToSave = new ArrayList<>();
        for (Map.Entry<Long, List<Long>> entry : subjectToTeachersMap.entrySet()) {
            Long subjectId = entry.getKey();
            List<Long> teacherIds = entry.getValue();

            Subject subject = subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new RuntimeException("Subject not found with ID: " + subjectId));

            for (Long teacherId : teacherIds) {
                Teacher teacher = teacherRepository.findById(teacherId)
                        .orElseThrow(() -> new RuntimeException("Teacher not found with ID: " + teacherId));
                
                TeacherSubjectAllocation allocation = new TeacherSubjectAllocation();
                allocation.setSubject(subject);
                allocation.setTeacher(teacher);
                allocationsToSave.add(allocation);
            }
        }
        
        List<TeacherSubjectAllocation> savedAllocations = allocationRepository.saveAll(allocationsToSave);
        return savedAllocations.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherSubjectAllocationDTO> getAllAllocations() {
        return allocationRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherSubjectAllocationDTO> getAllocationsByTeacher(Long teacherId) {
        return allocationRepository.findByTeacherId(teacherId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherSubjectAllocationDTO> getAllocationsBySubject(Long subjectId) {
        return allocationRepository.findBySubjectId(subjectId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private TeacherSubjectAllocationDTO mapToDTO(TeacherSubjectAllocation allocation) {
        TeacherSubjectAllocationDTO dto = new TeacherSubjectAllocationDTO();
        dto.setId(allocation.getId());
        dto.setTeacherId(allocation.getTeacher().getId());
        dto.setSubjectId(allocation.getSubject().getId());
        return dto;
    }
}

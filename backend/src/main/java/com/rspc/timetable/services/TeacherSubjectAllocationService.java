package com.rspc.timetable.services;

import com.rspc.timetable.dto.TeacherSubjectAllocationDTO;
import com.rspc.timetable.entities.Teacher;
import com.rspc.timetable.entities.TeacherSubjectAllocation;
import com.rspc.timetable.entities.TeacherSubjectAllocation.Role;
import com.rspc.timetable.repositories.TeacherRepository;
import com.rspc.timetable.repositories.TeacherSubjectAllocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeacherSubjectAllocationService {

    private final TeacherSubjectAllocationRepository repository;
    private final TeacherRepository teacherRepository;

    public TeacherSubjectAllocationService(TeacherSubjectAllocationRepository repository,
                                           TeacherRepository teacherRepository) {
        this.repository = repository;
        this.teacherRepository = teacherRepository;
    }

    // Legacy/DTO path retained for compatibility
    @Transactional
    public List<TeacherSubjectAllocation> saveAll(List<TeacherSubjectAllocationDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) return List.of();

        List<TeacherSubjectAllocation> rows = dtos.stream().map(dto -> {
            Objects.requireNonNull(dto.getSubjectId(), "subject_id is required");
            Objects.requireNonNull(dto.getTeacherId(), "teacher_id is required");
            Objects.requireNonNull(dto.getYearId(),    "year_id is required");

            TeacherSubjectAllocation t = new TeacherSubjectAllocation();
            t.setSubjectId(dto.getSubjectId());
            t.setTeacherId(dto.getTeacherId());
            t.setYearId(dto.getYearId());
            if (dto.getSemesterId() != null) t.setSemesterId(dto.getSemesterId());
            if (dto.getRole() != null) t.setRole(Role.valueOf(dto.getRole()));
            if (dto.getPriority() != null) t.setPriority(dto.getPriority());
            if (dto.getCapacityHours() != null) t.setCapacityHours(dto.getCapacityHours());
            t.setNotes(dto.getNotes());
            return t;
        }).toList();

        // Optional: validate teachers
        var teacherIds = rows.stream().map(TeacherSubjectAllocation::getTeacherId).distinct().toList();
        if (!teacherIds.isEmpty()) {
            int found = teacherRepository.findAllById(teacherIds).size();
            if (found != teacherIds.size()) {
                throw new IllegalArgumentException("One or more teacher_ids are invalid");
            }
        }

        return repository.saveAll(rows);
    }

    // New: persist fully-built rows coming from bulk-by-id path
    @Transactional
    public List<TeacherSubjectAllocation> saveAllEntities(List<TeacherSubjectAllocation> rows) {
        if (rows == null || rows.isEmpty()) return List.of();

        rows.forEach(r -> {
            Objects.requireNonNull(r.getSubjectId(), "subject_id required");
            Objects.requireNonNull(r.getTeacherId(), "teacher_id required");
            Objects.requireNonNull(r.getYearId(),    "year_id required");
        });

        // Validate teachers exist (recommended)
        var teacherIds = rows.stream().map(TeacherSubjectAllocation::getTeacherId)
                .filter(Objects::nonNull).distinct().toList();
        if (!teacherIds.isEmpty()) {
            int found = teacherRepository.findAllById(teacherIds).size();
            if (found != teacherIds.size()) {
                throw new IllegalArgumentException("One or more teacher_ids are invalid");
            }
        }

        // Optional dedup:
        // Map<String, TeacherSubjectAllocation> unique = new LinkedHashMap<>();
        // for (var r : rows) {
        //     String key = r.getSubjectId() + "-" + r.getTeacherId() + "-" + r.getRole();
        //     unique.putIfAbsent(key, r);
        // }
        // return repository.saveAll(unique.values());

        return repository.saveAll(rows);
    }

    @Transactional(readOnly = true)
    public List<Teacher> getTeachersForSubject(Long subjectId) {
        var rows = repository.findBySubjectId(subjectId);
        var ids = rows.stream().map(TeacherSubjectAllocation::getTeacherId)
                .filter(Objects::nonNull).distinct().toList();
        return ids.isEmpty() ? List.of() : teacherRepository.findAllById(ids);
    }

    @Transactional(readOnly = true)
    public List<Teacher> getTeachersForSubject(Long subjectId, Long yearId, String roleOrNull) {
        List<TeacherSubjectAllocation> rows = (roleOrNull != null)
                ? repository.findBySubjectIdAndYearIdAndRole(subjectId, yearId, Role.valueOf(roleOrNull))
                : repository.findBySubjectIdAndYearId(subjectId, yearId);
        var ids = rows.stream().map(TeacherSubjectAllocation::getTeacherId)
                .filter(Objects::nonNull).distinct().toList();
        return ids.isEmpty() ? List.of() : teacherRepository.findAllById(ids);
    }
}

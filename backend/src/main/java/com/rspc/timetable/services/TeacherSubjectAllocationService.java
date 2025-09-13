package com.rspc.timetable.services;

import com.rspc.timetable.dto.TeacherSubjectAllocationDTO;
import com.rspc.timetable.entities.TeacherSubjectAllocation;
import com.rspc.timetable.repositories.TeacherSubjectAllocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherSubjectAllocationService {

    private final TeacherSubjectAllocationRepository repository;

    public TeacherSubjectAllocationService(TeacherSubjectAllocationRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public List<TeacherSubjectAllocation> saveAll(List<TeacherSubjectAllocationDTO> dtos) {
        List<TeacherSubjectAllocation> allocations = dtos.stream().map(dto -> {
            TeacherSubjectAllocation t = new TeacherSubjectAllocation();
            t.setSubjectId(dto.getSubject_id());
            t.setTeacherId(dto.getTeacher_id());
            t.setYearId(dto.getYear_id());
            return t;
        }).collect(Collectors.toList());

        return repository.saveAll(allocations);
    }
}

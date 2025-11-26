package com.rspc.timetable.services;

import com.rspc.timetable.dto.SubjectDTO;
import com.rspc.timetable.entities.Semester;
import com.rspc.timetable.entities.Subject;
import com.rspc.timetable.repositories.SemesterRepository;
import com.rspc.timetable.repositories.SubjectRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final SemesterRepository semesterRepository;

    private void mapDtoToEntity(SubjectDTO dto, Subject entity) {

        entity.setName(dto.getName());
        entity.setCode(dto.getCode());

        // Category
        entity.setCategory(dto.getCategory());

        // Subject Type (Theory/Lab/Tutorial)
        entity.setSubjectType(dto.getSubjectType());

        // Semester
        Semester semester = semesterRepository.findById(dto.getSemesterId())
                .orElseThrow(() -> new EntityNotFoundException("Semester not found: " + dto.getSemesterId()));
        entity.setSemester(semester);
    }

    @Transactional(readOnly = true)
    public List<SubjectDTO> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(SubjectDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SubjectDTO getSubjectById(Long id) {
        return subjectRepository.findById(id)
                .map(SubjectDTO::new)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found with id: " + id));
    }

    @Transactional
    public SubjectDTO createSubject(SubjectDTO dto) {
        subjectRepository.findByCode(dto.getCode()).ifPresent(s -> {
            throw new IllegalArgumentException("Subject with code " + dto.getCode() + " already exists.");
        });

        Subject subject = new Subject();
        mapDtoToEntity(dto, subject);

        return new SubjectDTO(subjectRepository.save(subject));
    }

    @Transactional
    public List<SubjectDTO> createSubjectsBulk(List<SubjectDTO> dtoList) {
        List<Subject> entities = dtoList.stream().map(dto -> {
            Subject subject = new Subject();
            mapDtoToEntity(dto, subject);
            return subject;
        }).collect(Collectors.toList());

        return subjectRepository.saveAll(entities)
                .stream()
                .map(SubjectDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public SubjectDTO updateSubject(Long id, SubjectDTO dto) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found with id: " + id));

        mapDtoToEntity(dto, subject);

        return new SubjectDTO(subjectRepository.save(subject));
    }

    @Transactional
    public void deleteSubject(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new EntityNotFoundException("Subject not found with id: " + id);
        }
        subjectRepository.deleteById(id);
    }
}

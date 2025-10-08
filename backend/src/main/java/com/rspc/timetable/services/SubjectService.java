package com.rspc.timetable.services;

import com.rspc.timetable.dto.SubjectDTO;
import com.rspc.timetable.entities.Semester;
import com.rspc.timetable.entities.Subject;
import com.rspc.timetable.repositories.SemesterRepository;
import com.rspc.timetable.repositories.SubjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final SemesterRepository semesterRepository;

    public SubjectService(SubjectRepository subjectRepository, @Lazy SemesterRepository semesterRepository) {
        this.subjectRepository = subjectRepository;
        this.semesterRepository = semesterRepository;
    }

    private void mapDtoToEntity(SubjectDTO dto, Subject entity) {
        entity.setName(dto.getName());
        entity.setCode(dto.getCode());
        entity.setPriority(dto.getPriority());
        entity.setCategory(dto.getCategory());

        if (dto.getSemesterId() == null) {
            throw new IllegalArgumentException("Semester ID is required.");
        }
        Semester semester = semesterRepository.findById(dto.getSemesterId())
                .orElseThrow(() -> new EntityNotFoundException("Semester not found with id: " + dto.getSemesterId()));
        entity.setSemester(semester);
    }
    
    @Transactional(readOnly = true)
    public List<SubjectDTO> getAllSubjects() {
        return subjectRepository.findAll().stream().map(SubjectDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SubjectDTO getSubjectById(Long id) {
        return subjectRepository.findById(id).map(SubjectDTO::new)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found with id: " + id));
    }

    @Transactional
    public SubjectDTO createSubject(SubjectDTO subjectDTO) {
        subjectRepository.findByCode(subjectDTO.getCode()).ifPresent(s -> {
            throw new IllegalArgumentException("Subject with code " + subjectDTO.getCode() + " already exists.");
        });
        Subject newSubject = new Subject();
        mapDtoToEntity(subjectDTO, newSubject);
        return new SubjectDTO(subjectRepository.save(newSubject));
    }

    @Transactional
    public List<SubjectDTO> createSubjectsBulk(List<SubjectDTO> subjectDTOs) {
        List<Subject> subjectsToSave = subjectDTOs.stream().map(dto -> {
            Subject subject = new Subject();
            mapDtoToEntity(dto, subject);
            return subject;
        }).collect(Collectors.toList());
        return subjectRepository.saveAll(subjectsToSave).stream().map(SubjectDTO::new).collect(Collectors.toList());
    }

    @Transactional
    public SubjectDTO updateSubject(Long id, SubjectDTO subjectDTO) {
        Subject subjectToUpdate = subjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found with id: " + id));
        mapDtoToEntity(subjectDTO, subjectToUpdate);
        return new SubjectDTO(subjectRepository.save(subjectToUpdate));
    }

    @Transactional
    public void deleteSubject(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new EntityNotFoundException("Cannot delete. Subject not found with id: " + id);
        }
        subjectRepository.deleteById(id);
    }
}

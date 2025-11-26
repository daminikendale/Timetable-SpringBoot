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

    // Map DTO -> Entity (shared by create/update/bulk)
    private void mapDtoToEntity(SubjectDTO dto, Subject entity) {

        entity.setName(dto.getName());
        entity.setCode(dto.getCode());

        // --- Handle Category (REGULAR, HONORS, ELECTIVE) ---
        if (dto.getCategory() != null) {
            entity.setCategory(dto.getCategory());   // enums match directly
        } else {
            entity.setCategory(null);
        }

        // --- Handle SubjectType (THEORY, LAB, TUTORIAL) ---
        if (dto.getType() != null) {
            entity.setType(dto.getType());           // FIXED: setType()
        } else {
            entity.setType(null);
        }

        // --- Semester mapping ---
        if (dto.getSemesterId() == null) {
            throw new IllegalArgumentException("Semester ID is required.");
        }

        Semester semester = semesterRepository.findById(dto.getSemesterId())
            .orElseThrow(() ->
                new EntityNotFoundException("Semester not found with id: " + dto.getSemesterId())
            );

        entity.setSemester(semester);
    }

    @Transactional(readOnly = true)
    public List<SubjectDTO> getAllSubjects() {
        return subjectRepository.findAll()
                .stream()
                .map(SubjectDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SubjectDTO getSubjectById(Long id) {
        return subjectRepository.findById(id)
                .map(SubjectDTO::new)
                .orElseThrow(() -> 
                    new EntityNotFoundException("Subject not found with id: " + id)
                );
    }

    @Transactional
    public SubjectDTO createSubject(SubjectDTO subjectDTO) {

        subjectRepository.findByCode(subjectDTO.getCode()).ifPresent(s -> {
            throw new IllegalArgumentException(
                "Subject with code " + subjectDTO.getCode() + " already exists."
            );
        });

        Subject newSubject = new Subject();
        mapDtoToEntity(subjectDTO, newSubject);

        return new SubjectDTO(subjectRepository.save(newSubject));
    }

    @Transactional
    public List<SubjectDTO> createSubjectsBulk(List<SubjectDTO> dtos) {

        List<Subject> subjects = dtos.stream()
                .map(dto -> {
                    Subject s = new Subject();
                    mapDtoToEntity(dto, s);
                    return s;
                })
                .collect(Collectors.toList());

        return subjectRepository.saveAll(subjects)
                .stream()
                .map(SubjectDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public SubjectDTO updateSubject(Long id, SubjectDTO dto) {

        Subject entity = subjectRepository.findById(id)
                .orElseThrow(() ->
                    new EntityNotFoundException("Subject not found with id: " + id)
                );

        mapDtoToEntity(dto, entity);

        return new SubjectDTO(subjectRepository.save(entity));
    }

    @Transactional
    public void deleteSubject(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new EntityNotFoundException(
                "Cannot delete. Subject not found with id: " + id
            );
        }
        subjectRepository.deleteById(id);
    }
}

package com.rspc.timetable.services;

import com.rspc.timetable.dto.SemesterDivisionDTO;
import com.rspc.timetable.entities.Division;
import com.rspc.timetable.entities.Semester;
import com.rspc.timetable.entities.SemesterDivision;
import com.rspc.timetable.repositories.DivisionRepository;
import com.rspc.timetable.repositories.SemesterDivisionRepository;
import com.rspc.timetable.repositories.SemesterRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SemesterDivisionService {

    private final SemesterDivisionRepository semesterDivisionRepository;
    private final SemesterRepository semesterRepository;
    private final DivisionRepository divisionRepository;

    @Transactional
    public SemesterDivisionDTO createSemesterDivision(SemesterDivisionDTO dto) {
        Semester semester = semesterRepository.findById(dto.getSemesterId())
            .orElseThrow(() -> new EntityNotFoundException("Semester not found with id: " + dto.getSemesterId()));
        
        Division division = divisionRepository.findById(dto.getDivisionId())
            .orElseThrow(() -> new EntityNotFoundException("Division not found with id: " + dto.getDivisionId()));

        SemesterDivision newMapping = new SemesterDivision();
        newMapping.setSemester(semester);
        newMapping.setDivision(division);

        SemesterDivision savedMapping = semesterDivisionRepository.save(newMapping);
        return new SemesterDivisionDTO(savedMapping);
    }

    @Transactional(readOnly = true)
    public List<SemesterDivisionDTO> getDivisionsForSemester(Long semesterId) {
        if (!semesterRepository.existsById(semesterId)) {
            throw new EntityNotFoundException("Semester not found with id: " + semesterId);
        }
        return semesterDivisionRepository.findBySemesterId(semesterId).stream()
            .map(SemesterDivisionDTO::new)
            .collect(Collectors.toList());
    }

    @Transactional
    public void deleteSemesterDivision(Long id) {
        if (!semesterDivisionRepository.existsById(id)) {
            throw new EntityNotFoundException("SemesterDivision mapping not found with id: " + id);
        }
        semesterDivisionRepository.deleteById(id);
    }

    @Transactional
    public List<SemesterDivisionDTO> createSemesterDivisions(List<SemesterDivisionDTO> dtos) {
    List<SemesterDivisionDTO> createdList = new ArrayList<>();
    for (SemesterDivisionDTO dto : dtos) {
        createdList.add(createSemesterDivision(dto)); // reuse existing single-create method
    }
    return createdList;
}

}

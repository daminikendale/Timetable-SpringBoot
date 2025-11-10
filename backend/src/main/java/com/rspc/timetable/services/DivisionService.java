package com.rspc.timetable.services;

import com.rspc.timetable.dto.DivisionDTO;
import com.rspc.timetable.entities.Division;
import com.rspc.timetable.repositories.DivisionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DivisionService {

    private final DivisionRepository divisionRepository;

    private DivisionDTO convertToDTO(Division division) {
        return new DivisionDTO(division.getId(), division.getDivisionName());
    }

    @Transactional(readOnly = true)
    public List<DivisionDTO> getAllDivisions() {
        return divisionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ✅ NEW METHOD: Get divisions by year ID
    @Transactional(readOnly = true)
    public List<DivisionDTO> getDivisionsByYearId(Long yearId) {
        return divisionRepository.findByYearId(yearId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DivisionDTO getDivisionById(Long id) {
        return divisionRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Division not found with id: " + id));
    }

    @Transactional
    public DivisionDTO createDivision(DivisionDTO divisionDTO) {
        if (divisionRepository.findByDivisionName(divisionDTO.getDivisionName()).isPresent()) {
            throw new IllegalArgumentException("Division already exists.");
        }

        Division newDivision = new Division();
        newDivision.setDivisionName(divisionDTO.getDivisionName());
        return convertToDTO(divisionRepository.save(newDivision));
    }

    @Transactional
    public DivisionDTO updateDivision(Long id, DivisionDTO divisionDTO) {
        Division division = divisionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Division not found"));
        division.setDivisionName(divisionDTO.getDivisionName());
        return convertToDTO(divisionRepository.save(division));
    }

    @Transactional
    public void deleteDivision(Long id) {
        divisionRepository.deleteById(id);
    }
}

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

    // Helper method to convert an entity to a DTO
    private DivisionDTO convertToDTO(Division division) {
        return new DivisionDTO(division.getId(), division.getDivisionName());
    }

    @Transactional(readOnly = true)
    public List<DivisionDTO> getAllDivisions() {
        return divisionRepository.findAll().stream()
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
        // Prevent creating a division with a duplicate name
        if (divisionRepository.findByDivisionName(divisionDTO.getDivisionName()).isPresent()) {
            throw new IllegalArgumentException("Division with name '" + divisionDTO.getDivisionName() + "' already exists.");
        }
        
        Division newDivision = new Division();
        newDivision.setDivisionName(divisionDTO.getDivisionName());
        
        Division savedDivision = divisionRepository.save(newDivision);
        return convertToDTO(savedDivision);
    }
    
    @Transactional
    public DivisionDTO updateDivision(Long id, DivisionDTO divisionDTO) {
        Division divisionToUpdate = divisionRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Division not found with id: " + id));
            
        divisionToUpdate.setDivisionName(divisionDTO.getDivisionName());
        
        Division updatedDivision = divisionRepository.save(divisionToUpdate);
        return convertToDTO(updatedDivision);
    }

    @Transactional
    public void deleteDivision(Long id) {
        if (!divisionRepository.existsById(id)) {
            throw new EntityNotFoundException("Cannot delete. Division not found with id: " + id);
        }
        divisionRepository.deleteById(id);
    }
    
    @Transactional
    public List<DivisionDTO> saveAllDivisions(List<DivisionDTO> divisionDTOs) {
        List<Division> divisions = divisionDTOs.stream().map(dto -> {
            Division division = new Division();
            division.setDivisionName(dto.getDivisionName());
            return division;
        }).collect(Collectors.toList());
        
        List<Division> savedDivisions = divisionRepository.saveAll(divisions);
        
        return savedDivisions.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
}

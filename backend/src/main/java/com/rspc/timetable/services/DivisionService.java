package com.rspc.timetable.services;

import com.rspc.timetable.dto.DivisionDTO;
import com.rspc.timetable.entities.Division;
import com.rspc.timetable.repositories.DivisionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DivisionService {

    private final DivisionRepository divisionRepository;

    public DivisionService(DivisionRepository divisionRepository) {
        this.divisionRepository = divisionRepository;
    }

    public List<Division> getAllDivisions() {
        return divisionRepository.findAll();
    }

    public Optional<Division> getDivisionById(Long id) {
        return divisionRepository.findById(id);
    }

    public Division saveDivision(Division division) {
        return divisionRepository.save(division);
    }

    public void deleteDivision(Long id) {
        divisionRepository.deleteById(id);
    }

    // Convert all divisions to DTOs
    public List<DivisionDTO> getAllDivisionDTOs() {
        return divisionRepository.findAll().stream()
                .map(d -> new DivisionDTO(
                        d.getId(),
                        d.getName(),
                        d.getYear() != null ? d.getYear().getYearNumber() : 0,
                        d.getSemester() != null ? d.getSemester().getSemesterNumber() : 0  // add semester number
                ))
                .collect(Collectors.toList());
    }

    public List<Division> saveAllDivisions(List<Division> divisions) {
        return divisionRepository.saveAll(divisions);
    }


}

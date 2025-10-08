package com.rspc.timetable.services;

import com.rspc.timetable.entities.Year;
import com.rspc.timetable.repositories.YearRepository;
import com.rspc.timetable.dto.YearDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class YearService {

    private final YearRepository yearRepository;

    public List<Year> getAllYears() {
        return yearRepository.findAll();
    }

    public Optional<Year> getYearById(Long id) {
        return yearRepository.findById(id);
    }

    public List<Year> saveYears(List<Year> years) {
        return yearRepository.saveAll(years);
    }

    public Year saveYear(Year year) {
        return yearRepository.save(year);
    }

    public void deleteYear(Long id) {
        yearRepository.deleteById(id);
    }

    /**
     * Correctly maps Year entities to the updated YearDTO.
     */
    public List<YearDTO> getAllYearDTOs() {
        return yearRepository.findAll().stream()
                .map(YearDTO::new) // Uses the smart constructor from the DTO
                .collect(Collectors.toList());
    }

    public List<Year> findAllByIds(Set<Long> ids) {
        return yearRepository.findAllById(ids);
    }
}

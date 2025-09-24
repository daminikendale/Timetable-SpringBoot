package com.rspc.timetable.services;

import com.rspc.timetable.entities.Year;
import com.rspc.timetable.repositories.YearRepository;
import org.springframework.stereotype.Service;

import com.rspc.timetable.dto.YearDTO;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Set;
import java.util.Optional;

@Service
public class YearService {

    private final YearRepository yearRepository;

    public YearService(YearRepository yearRepository) {
        this.yearRepository = yearRepository;
    }

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

    public List<YearDTO> getAllYearDTOs() {
        return yearRepository.findAll().stream()
                .map(y -> new YearDTO(y.getId(), y.getYearNumber()))
                .collect(Collectors.toList());
    }

    public List<Year> findAllByIds(Set<Long> ids) {
    // If you already have a repository, delegate.
    return yearRepository.findAllById(ids);
}

}

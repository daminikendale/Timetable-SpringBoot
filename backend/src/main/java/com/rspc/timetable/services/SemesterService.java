package com.rspc.timetable.services;

import com.rspc.timetable.entities.Semester;
import com.rspc.timetable.repositories.SemesterRepository;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.List;
import java.util.Optional;
import com.rspc.timetable.dto.SemesterDTO;
import java.util.stream.Collectors;

@Service
public class SemesterService {
    private final SemesterRepository semesterRepository;

    public SemesterService(SemesterRepository semesterRepository) {
        this.semesterRepository = semesterRepository;
    }

    public List<SemesterDTO> getAllSemesterDTOs() {
    return semesterRepository.findAll().stream()
        .map(s -> SemesterDTO.builder()
            .id(s.getId())
            .semesterNumber(s.getSemesterNumber())
            .year_id(s.getYear() != null ? s.getYear().getId() : null)
            .yearNumber(s.getYear() != null ? s.getYear().getYearNumber() : null)
            .build())
        .collect(Collectors.toList());
}
    public List<Semester> getAllSemesters() { return semesterRepository.findAll(); }
    public Optional<Semester> getSemesterById(Long id) { return semesterRepository.findById(id); }
    public Semester saveSemester(Semester semester) { return semesterRepository.save(semester); }
    public List<Semester> saveSemesters(List<Semester> semesters) { return semesterRepository.saveAll(semesters); }
    public void deleteSemester(Long id) { semesterRepository.deleteById(id); }

    public List<Semester> findAllByIds(Set<Long> ids) {
    return semesterRepository.findAllById(ids);
}
}

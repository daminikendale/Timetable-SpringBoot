// src/main/java/com/rspc/timetable/services/SubjectService.java
package com.rspc.timetable.services;

import com.rspc.timetable.dto.SubjectDTO;
import com.rspc.timetable.entities.*;
import com.rspc.timetable.repositories.SubjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final YearService yearService;
    private final SemesterService semesterService;

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    @Transactional
    public List<Subject> createOrUpdateBulk(List<SubjectDTO> dtos) {
        Set<Long> yearIds = dtos.stream().map(SubjectDTO::getYearId).collect(Collectors.toSet());
        Set<Long> semIds  = dtos.stream().map(SubjectDTO::getSemesterId).collect(Collectors.toSet());

        Map<Long, Year> years = yearService.findAllByIds(yearIds).stream()
                .collect(Collectors.toMap(Year::getId, y -> y));
        Map<Long, Semester> sems = semesterService.findAllByIds(semIds).stream()
                .collect(Collectors.toMap(Semester::getId, s -> s));

        List<Subject> batch = new ArrayList<>(dtos.size());
        for (SubjectDTO d : dtos) {
            Subject s = subjectRepository.findByCode(d.getCode()).orElseGet(Subject::new);
            s.setCode(d.getCode());
            s.setName(d.getName());

            // Convert type string -> enum
            try {
                s.setType(SubjectType.valueOf(d.getType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid SubjectType: " + d.getType());
            }

            s.setCredits(d.getCredits());

            // Convert category string -> enum
            try {
                s.setCategory(SubjectCategory.valueOf(d.getCategory().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid SubjectCategory: " + d.getCategory());
            }

            Year y = years.get(d.getYearId());
            if (y == null) throw new IllegalArgumentException("Invalid yearId: " + d.getYearId());
            s.setYear(y);

            Semester sem = sems.get(d.getSemesterId());
            if (sem == null) throw new IllegalArgumentException("Invalid semesterId: " + d.getSemesterId());
            s.setSemester(sem);

            batch.add(s);
        }
        return subjectRepository.saveAll(batch);
    }

    public Subject updateCredits(Long id, Integer credits) {
        Subject s = subjectRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Subject not found: " + id));
        s.setCredits(credits);
        return subjectRepository.save(s);
    }
}

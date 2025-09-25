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

        // First pass: build/refresh entities and collect for ranking if priority missing
        List<Subject> batch = new ArrayList<>(dtos.size());
        record Pending(Subject s, int difficulty) {}
        Map<String, List<Pending>> groupForRank = new HashMap<>();

        for (SubjectDTO d : dtos) {
            Subject s = subjectRepository.findByCode(d.getCode()).orElseGet(Subject::new);
            s.setCode(d.getCode());
            s.setName(d.getName());

            try { s.setType(SubjectType.valueOf(d.getType().toUpperCase())); }
            catch (IllegalArgumentException e) { throw new IllegalArgumentException("Invalid SubjectType: " + d.getType()); }

            s.setCredits(d.getCredits());

            try { s.setCategory(SubjectCategory.valueOf(d.getCategory().toUpperCase())); }
            catch (IllegalArgumentException e) { throw new IllegalArgumentException("Invalid SubjectCategory: " + d.getCategory()); }

            Year y = years.get(d.getYearId());
            if (y == null) throw new IllegalArgumentException("Invalid yearId: " + d.getYearId());
            s.setYear(y);

            Semester sem = sems.get(d.getSemesterId());
            if (sem == null) throw new IllegalArgumentException("Invalid semesterId: " + d.getSemesterId());
            s.setSemester(sem);

            if (d.getPriority() != null) {
                s.setPriority(d.getPriority());
            } else {
                // compute difficulty: credits + categoryWeight + typeWeight
                int categoryWeight = switch (s.getCategory()) {
                    case REGULAR -> 2;
                    case PROGRAM_ELECTIVE -> 1;
                    case OPEN_ELECTIVE -> 0;
                };
                int typeWeight = (s.getType() == SubjectType.THEORY) ? 1 : 0;
                int difficulty = s.getCredits() + categoryWeight + typeWeight;
                String key = y.getId() + "-" + sem.getId();
                groupForRank.computeIfAbsent(key, k -> new ArrayList<>()).add(new Pending(s, difficulty));
            }

            batch.add(s);
        }

        // Second pass: rank within each year-sem by difficulty desc, assign priority 1..N
        for (var e : groupForRank.entrySet()) {
            List<Pending> list = e.getValue();
            list.sort(Comparator.<Pending>comparingInt(p -> -p.difficulty)); // higher difficulty first
            int rank = 1;
            for (Pending p : list) {
                p.s.setPriority(rank++);
            }
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

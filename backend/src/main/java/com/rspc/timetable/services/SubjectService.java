package com.rspc.timetable.services;

import com.rspc.timetable.dto.SubjectDTO;
import com.rspc.timetable.entities.Semester;
import com.rspc.timetable.entities.Subject;
import com.rspc.timetable.entities.SubjectCategory;
import com.rspc.timetable.entities.Year;
import com.rspc.timetable.repositories.SemesterRepository;
import com.rspc.timetable.repositories.SubjectRepository;
import com.rspc.timetable.repositories.YearRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import com.rspc.timetable.dto.SubjectDTO;
@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final SemesterRepository semesterRepository;
    private final YearRepository yearRepository;

    public SubjectService(SubjectRepository subjectRepository,
                          SemesterRepository semesterRepository,
                          YearRepository yearRepository) {
        this.subjectRepository = subjectRepository;
        this.semesterRepository = semesterRepository;
        this.yearRepository = yearRepository;
    }

    public Subject saveSubject(SubjectDTO dto) {
        Semester semester = semesterRepository.findById(dto.getSemester_id())
                .orElseThrow(() -> new RuntimeException("Semester not found: " + dto.getSemester_id()));
        Year year = yearRepository.findById(dto.getYear_id())
                .orElseThrow(() -> new RuntimeException("Year not found: " + dto.getYear_id()));

        Subject subject = new Subject();
        subject.setName(dto.getName());
        subject.setType(dto.getType());
        subject.setCredits(dto.getCredits());
        subject.setCategory(SubjectCategory.valueOf(dto.getCategory().toUpperCase()));
        subject.setSemester(semester);
        subject.setYear(year);

        return subjectRepository.save(subject);
    }

    public List<Subject> saveAllSubjects(List<SubjectDTO> dtos) {
        return dtos.stream()
                .map(this::saveSubject)
                .collect(Collectors.toList());
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    // ✅ Add this so your TimetableController doesn’t break
    public Subject getSubjectById(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found: " + id));
    }

    public List<SubjectDTO> getAllSubjectDTOs() {
    return subjectRepository.findAll().stream()
        .map(s -> SubjectDTO.builder()
            .id(s.getId())
            .name(s.getName())
            .type(s.getType())
            .credits(s.getCredits())
            .category(s.getCategory() != null ? s.getCategory().name() : null)
            .year_id(s.getYear() != null ? s.getYear().getId() : null)
            .semester_id(s.getSemester() != null ? s.getSemester().getId() : null)
            .build())
        .collect(Collectors.toList());
}
}

package com.rspc.timetable.services;

import com.rspc.timetable.entities.CourseOffering;
import com.rspc.timetable.repositories.CourseOfferingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseOfferingService {
    private final CourseOfferingRepository repo;

    public CourseOffering defineOffering(CourseOffering co) {
        return repo.save(co);
    }

    public List<CourseOffering> defineOfferings(List<CourseOffering> offerings) {
        return repo.saveAll(offerings);
    }

    // ID-based existence check without division
    public boolean existsBySubjectYearSemester(Long subjectId, Long yearId, Long semesterId) {
        return repo.existsBySubjectIdAndYearIdAndSemesterId(subjectId, yearId, semesterId);
    }

    public List<CourseOffering> getAll() {
        return repo.findAll();
    }

    // Removed: forDivision(Long divisionId)

    public List<CourseOffering> forSemester(Long yearId, Long semId) {
        return repo.findByYearIdAndSemesterId(yearId, semId);
    }
}

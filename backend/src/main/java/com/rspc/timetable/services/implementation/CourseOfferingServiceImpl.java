package com.rspc.timetable.services.implementation;

import com.rspc.timetable.dto.CourseOfferingDTO;
import com.rspc.timetable.entities.*;
import com.rspc.timetable.repositories.*;
import com.rspc.timetable.services.CourseOfferingService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseOfferingServiceImpl implements CourseOfferingService {

    private final CourseOfferingRepository courseOfferingRepository;
    private final SubjectRepository subjectRepository;
    private final DepartmentRepository departmentRepository;
    private final YearRepository yearRepository;
    private final SemesterRepository semesterRepository;

    @Override
    @Transactional
    public CourseOfferingDTO createCourseOffering(CourseOfferingDTO dto) {
        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new EntityNotFoundException("Subject not found: " + dto.getSubjectId()));
        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new EntityNotFoundException("Department not found: " + dto.getDepartmentId()));
        Year year = yearRepository.findById(dto.getYearId())
                .orElseThrow(() -> new EntityNotFoundException("Year not found: " + dto.getYearId()));
        Semester semester = semesterRepository.findById(dto.getSemesterId())
                .orElseThrow(() -> new EntityNotFoundException("Semester not found: " + dto.getSemesterId()));

        CourseOffering offering = new CourseOffering();
        offering.setSubject(subject);
        offering.setDepartment(department);
        offering.setYear(year);
        offering.setSemester(semester);
        offering.setLecPerWeek(dto.getLecPerWeek());
        offering.setTutPerWeek(dto.getTutPerWeek());
        offering.setLabPerWeek(dto.getLabPerWeek());
        offering.setWeeklyHours(dto.getWeeklyHours());

        return new CourseOfferingDTO(courseOfferingRepository.save(offering));
    }

    @Override
    @Transactional
    public List<CourseOfferingDTO> createBulkCourseOfferings(List<CourseOfferingDTO> dtos) {
        List<CourseOffering> offerings = dtos.stream().map(dto -> {
            Subject subject = subjectRepository.findById(dto.getSubjectId())
                    .orElseThrow(() -> new EntityNotFoundException("Subject not found: " + dto.getSubjectId()));
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new EntityNotFoundException("Department not found: " + dto.getDepartmentId()));
            Year year = yearRepository.findById(dto.getYearId())
                    .orElseThrow(() -> new EntityNotFoundException("Year not found: " + dto.getYearId()));
            Semester semester = semesterRepository.findById(dto.getSemesterId())
                    .orElseThrow(() -> new EntityNotFoundException("Semester not found: " + dto.getSemesterId()));

            CourseOffering offering = new CourseOffering();
            offering.setSubject(subject);
            offering.setDepartment(department);
            offering.setYear(year);
            offering.setSemester(semester);
            offering.setLecPerWeek(dto.getLecPerWeek());
            offering.setTutPerWeek(dto.getTutPerWeek());
            offering.setLabPerWeek(dto.getLabPerWeek());
            offering.setWeeklyHours(dto.getWeeklyHours());
            return offering;
        }).collect(Collectors.toList());

        return courseOfferingRepository.saveAll(offerings)
                .stream().map(CourseOfferingDTO::new).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseOfferingDTO> getAllOfferings() {
        return courseOfferingRepository.findAll().stream()
                .map(CourseOfferingDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CourseOfferingDTO getOfferingById(Long id) {
        return courseOfferingRepository.findById(id)
                .map(CourseOfferingDTO::new)
                .orElseThrow(() -> new EntityNotFoundException("Course Offering not found: " + id));
    }

    @Override
    @Transactional
    public void deleteOffering(Long id) {
        if (!courseOfferingRepository.existsById(id)) {
            throw new EntityNotFoundException("Course Offering not found: " + id);
        }
        courseOfferingRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseOfferingDTO> getOfferingsBySemesterNumbers(List<Integer> semesterNumbers) {
        // ✅ FIX: Using the correct Spring Data JPA conventional method name
        return courseOfferingRepository.findBySubject_Semester_SemesterNumberIn(semesterNumbers)
                .stream().map(CourseOfferingDTO::new).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseOfferingDTO> getOfferingsBySubjectAndSemesters(Long subjectId, List<Integer> semesterNumbers) {
        // ✅ FIX: Using the correct Spring Data JPA conventional method name
        return courseOfferingRepository.findBySubjectIdAndSemester_SemesterNumberIn(subjectId, semesterNumbers)
                .stream().map(CourseOfferingDTO::new).collect(Collectors.toList());
    }
}

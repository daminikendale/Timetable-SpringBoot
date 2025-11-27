package com.rspc.timetable.services.implementation;

import com.rspc.timetable.dto.CourseOfferingDTO;
import com.rspc.timetable.entities.CourseOffering;
import com.rspc.timetable.entities.Department;
import com.rspc.timetable.entities.Semester;
import com.rspc.timetable.entities.Subject;
import com.rspc.timetable.entities.Year;
import com.rspc.timetable.repositories.CourseOfferingRepository;
import com.rspc.timetable.repositories.DepartmentRepository;
import com.rspc.timetable.repositories.SemesterRepository;
import com.rspc.timetable.repositories.SubjectRepository;
import com.rspc.timetable.repositories.YearRepository;
import com.rspc.timetable.services.CourseOfferingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CourseOfferingServiceImpl implements CourseOfferingService {

    private final CourseOfferingRepository courseOfferingRepository;
    private final SubjectRepository subjectRepository;
    private final DepartmentRepository departmentRepository;
    private final YearRepository yearRepository;
    private final SemesterRepository semesterRepository;

    public CourseOfferingServiceImpl(CourseOfferingRepository courseOfferingRepository,
                                     SubjectRepository subjectRepository,
                                     DepartmentRepository departmentRepository,
                                     YearRepository yearRepository,
                                     SemesterRepository semesterRepository) {
        this.courseOfferingRepository = courseOfferingRepository;
        this.subjectRepository = subjectRepository;
        this.departmentRepository = departmentRepository;
        this.yearRepository = yearRepository;
        this.semesterRepository = semesterRepository;
    }

    @Override
    @Transactional
    public CourseOfferingDTO createCourseOffering(CourseOfferingDTO dto) {
        CourseOffering saved = courseOfferingRepository.save(buildFromDto(dto));
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseOfferingDTO getOfferingById(Long id) {
        return courseOfferingRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("CourseOffering not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseOfferingDTO> getOfferingsBySubjectAndSemesters(Long subjectId, List<Integer> semesterNumbers) {
        Set<Integer> wanted = Set.copyOf(semesterNumbers == null ? List.of() : semesterNumbers);
        return courseOfferingRepository.findAll().stream()
                .filter(co -> co.getSubject() != null && Objects.equals(co.getSubject().getId(), subjectId))
                .filter(co -> {
                    Semester sem = co.getSemester();
                    Integer num = semesterNumber(sem);
                    return sem != null && (wanted.isEmpty() || (num != null && wanted.contains(num)));
                })
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseOfferingDTO> getOfferingsBySemesterNumbers(List<Integer> semesterNumbers) {
        Set<Integer> wanted = Set.copyOf(semesterNumbers == null ? List.of() : semesterNumbers);
        return courseOfferingRepository.findAll().stream()
                .filter(co -> {
                    Semester sem = co.getSemester();
                    Integer num = semesterNumber(sem);
                    return sem != null && (wanted.isEmpty() || (num != null && wanted.contains(num)));
                })
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseOfferingDTO> getAllOfferings() {
        return courseOfferingRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // --------- Build and map ---------

    private CourseOffering buildFromDto(CourseOfferingDTO dto) {
        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new IllegalArgumentException("Subject not found: " + dto.getSubjectId()));
        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("Department not found: " + dto.getDepartmentId()));
        Year year = yearRepository.findById(dto.getYearId())
                .orElseThrow(() -> new IllegalArgumentException("Year not found: " + dto.getYearId()));
        Semester semester = semesterRepository.findById(dto.getSemesterId())
                .orElseThrow(() -> new IllegalArgumentException("Semester not found: " + dto.getSemesterId()));

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
    }

    private CourseOfferingDTO toDto(CourseOffering offering) {
        CourseOfferingDTO d = new CourseOfferingDTO();
        d.setId(offering.getId());
        d.setSubjectId(offering.getSubject() != null ? offering.getSubject().getId() : null);
        d.setDepartmentId(offering.getDepartment() != null ? offering.getDepartment().getId() : null);
        d.setYearId(offering.getYear() != null ? offering.getYear().getId() : null);
        d.setSemesterId(offering.getSemester() != null ? offering.getSemester().getId() : null);
        d.setLecPerWeek(offering.getLecPerWeek());
        d.setTutPerWeek(offering.getTutPerWeek());
        d.setLabPerWeek(offering.getLabPerWeek());
        d.setWeeklyHours(offering.getWeeklyHours());
        return d;
    }

    // Robust extractor for semester number
    private Integer semesterNumber(Semester sem) {
        if (sem == null) return null;
        for (String name : new String[]{"getNumber", "getSemesterNo", "getSemNo", "getSemNumber", "getValue"}) {
            try {
                Method m = sem.getClass().getMethod(name);
                Object v = m.invoke(sem);
                if (v instanceof Integer) return (Integer) v;
                if (v instanceof Number) return ((Number) v).intValue();
                if (v != null) {
                    try {
                        return Integer.parseInt(String.valueOf(v));
                    } catch (NumberFormatException ignored) {
                    }
                }
            } catch (ReflectiveOperationException ignored) {
            }
        }
        return null;
    }

    @Override
    @Transactional
    public List<CourseOfferingDTO> createBulkCourseOfferings(List<CourseOfferingDTO> offerings) {
        List<CourseOffering> entities = offerings.stream()
                .map(this::buildFromDto)
                .collect(Collectors.toList());
        return courseOfferingRepository.saveAll(entities).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteOffering(Long id) {
        if (!courseOfferingRepository.existsById(id)) {
            throw new IllegalArgumentException("CourseOffering not found: " + id);
        }
        courseOfferingRepository.deleteById(id);
    }
}

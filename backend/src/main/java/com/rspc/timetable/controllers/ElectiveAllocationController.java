package com.rspc.timetable.controllers;

import com.rspc.timetable.entities.ElectiveAllocation;
import com.rspc.timetable.entities.Subject;
import com.rspc.timetable.entities.Teacher;
import com.rspc.timetable.entities.Year;
import com.rspc.timetable.entities.Semester;
import com.rspc.timetable.services.ElectiveAllocationHelper;
import com.rspc.timetable.services.ElectiveAllocationService;
import com.rspc.timetable.repositories.SubjectRepository;
import com.rspc.timetable.repositories.YearRepository;
import com.rspc.timetable.repositories.SemesterRepository;
import com.rspc.timetable.repositories.TeacherRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/elective-allocations")
public class ElectiveAllocationController {

    private final ElectiveAllocationService service;
    private final ElectiveAllocationHelper helper;
    private final SubjectRepository subjectRepo;
    private final YearRepository yearRepo;
    private final SemesterRepository semesterRepo;
    private final TeacherRepository teacherRepo;

    public ElectiveAllocationController(ElectiveAllocationService service,
                                        ElectiveAllocationHelper helper,
                                        SubjectRepository subjectRepo,
                                        YearRepository yearRepo,
                                        SemesterRepository semesterRepo,
                                        TeacherRepository teacherRepo) {
        this.service = service;
        this.helper = helper;
        this.subjectRepo = subjectRepo;
        this.yearRepo = yearRepo;
        this.semesterRepo = semesterRepo;
        this.teacherRepo = teacherRepo;
    }

    // Fetch all elective allocations
    @GetMapping
    public List<ElectiveAllocation> getAll() {
        return service.getAll();
    }

    // Create multiple allocations manually
    @PostMapping("/bulk")
    public List<ElectiveAllocation> createBulk(@RequestBody List<ElectiveAllocation> allocations) {
        return service.saveAll(allocations);
    }

    // Create a single allocation manually
    @PostMapping
    public ElectiveAllocation create(@RequestBody ElectiveAllocation allocation) {
        return service.save(allocation);
    }

    // Delete allocation by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ===== New endpoint: Auto-generate elective groups based on student count =====
    @PostMapping("/auto-allocate")
    public ResponseEntity<List<ElectiveAllocation>> autoAllocate(
            @RequestParam Long subjectId,
            @RequestParam Long yearId,
            @RequestParam Long semesterId,
            @RequestParam Long teacherId,
            @RequestParam int totalStudents,
            @RequestParam int maxCapacity
    ) {
        // Fetch required entities from DB
        Subject subject = subjectRepo.findById(subjectId).orElseThrow(() -> new RuntimeException("Subject not found"));
        Year year = yearRepo.findById(yearId).orElseThrow(() -> new RuntimeException("Year not found"));
        Semester semester = semesterRepo.findById(semesterId).orElseThrow(() -> new RuntimeException("Semester not found"));
        Teacher teacher = teacherRepo.findById(teacherId).orElseThrow(() -> new RuntimeException("Teacher not found"));

        List<ElectiveAllocation> allocations = helper.createElectiveGroups(subject, year, semester, teacher, totalStudents, maxCapacity);
        return ResponseEntity.ok(allocations);
    }
}

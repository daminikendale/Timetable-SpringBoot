package com.rspc.timetable.controllers;

import com.rspc.timetable.entities.ElectiveGroup;
import com.rspc.timetable.entities.Semester;
import com.rspc.timetable.repositories.ElectiveGroupRepository;
import com.rspc.timetable.repositories.SemesterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/elective-groups")
public class ElectiveGroupController {

    @Autowired
    private ElectiveGroupRepository electiveGroupRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    // DTO for the request body
    public static class ElectiveGroupRequest {
        private String name;
        private Long semesterId;
        private String notes;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Long getSemesterId() { return semesterId; }
        public void setSemesterId(Long semesterId) { this.semesterId = semesterId; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    /**
     * Creates multiple elective groups in a single transaction.
     */
    @PostMapping("/bulk")
    public ResponseEntity<List<ElectiveGroup>> createElectiveGroupsBulk(@RequestBody List<ElectiveGroupRequest> requests) {
        // Fetch all required semesters in one query
        List<Long> semesterIds = requests.stream()
                .map(ElectiveGroupRequest::getSemesterId)
                .distinct()
                .collect(Collectors.toList());
        
        Map<Long, Semester> semesterMap = semesterRepository.findAllById(semesterIds).stream()
                .collect(Collectors.toMap(Semester::getId, semester -> semester));

        List<ElectiveGroup> groupsToSave = new ArrayList<>();
        for (ElectiveGroupRequest request : requests) {
            Semester semester = semesterMap.get(request.getSemesterId());
            if (semester == null) {
                // If any semester ID is invalid, fail the entire request
                return ResponseEntity.badRequest().build();
            }

            ElectiveGroup group = new ElectiveGroup();
            group.setName(request.getName());
            group.setSemester(semester);
            group.setNotes(request.getNotes());
            groupsToSave.add(group);
        }

        List<ElectiveGroup> savedGroups = electiveGroupRepository.saveAll(groupsToSave);
        return ResponseEntity.ok(savedGroups);
    }
    
    // Other endpoints from the previous example...
    @GetMapping
    public List<ElectiveGroup> getAllElectiveGroups() {
        return electiveGroupRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ElectiveGroup> getElectiveGroupById(@PathVariable Long id) {
        return electiveGroupRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

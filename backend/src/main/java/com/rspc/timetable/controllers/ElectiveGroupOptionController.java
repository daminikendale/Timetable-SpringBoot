package com.rspc.timetable.controllers;

import com.rspc.timetable.entities.ElectiveGroup;
import com.rspc.timetable.entities.ElectiveGroupOption;
import com.rspc.timetable.entities.Subject;
import com.rspc.timetable.repositories.ElectiveGroupOptionRepository;
import com.rspc.timetable.repositories.ElectiveGroupRepository;
import com.rspc.timetable.repositories.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/elective-group-options")
public class ElectiveGroupOptionController {

    @Autowired
    private ElectiveGroupOptionRepository optionRepository;
    @Autowired
    private ElectiveGroupRepository groupRepository;
    @Autowired
    private SubjectRepository subjectRepository;

    // DTO for the request body
    public static class ElectiveGroupOptionRequest {
        private Long electiveGroupId;
        private Long subjectId;
        private Integer minIntake;
        private Integer maxIntake;

        // Getters and Setters
        public Long getElectiveGroupId() { return electiveGroupId; }
        public void setElectiveGroupId(Long electiveGroupId) { this.electiveGroupId = electiveGroupId; }
        public Long getSubjectId() { return subjectId; }
        public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
        public Integer getMinIntake() { return minIntake; }
        public void setMinIntake(Integer minIntake) { this.minIntake = minIntake; }
        public Integer getMaxIntake() { return maxIntake; }
        public void setMaxIntake(Integer maxIntake) { this.maxIntake = maxIntake; }
    }

    /**
     * Creates multiple elective group options in a single request.
     */
    @PostMapping("/bulk")
    public ResponseEntity<List<ElectiveGroupOption>> createBulkOptions(@RequestBody List<ElectiveGroupOptionRequest> requests) {
        // Fetch all necessary parent entities in batch to avoid N+1 queries
        Map<Long, ElectiveGroup> groupMap = groupRepository.findAllById(
                requests.stream().map(ElectiveGroupOptionRequest::getElectiveGroupId).collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(ElectiveGroup::getId, group -> group));

        Map<Long, Subject> subjectMap = subjectRepository.findAllById(
                requests.stream().map(ElectiveGroupOptionRequest::getSubjectId).collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(Subject::getId, subject -> subject));

        List<ElectiveGroupOption> optionsToSave = new ArrayList<>();
        for (ElectiveGroupOptionRequest req : requests) {
            ElectiveGroup group = groupMap.get(req.getElectiveGroupId());
            Subject subject = subjectMap.get(req.getSubjectId());

            if (group == null || subject == null) {
                // If any foreign key is invalid, fail the entire batch
                return ResponseEntity.badRequest().build();
            }

            ElectiveGroupOption option = new ElectiveGroupOption();
            option.setElectiveGroup(group);
            option.setSubject(subject);
            option.setMinIntake(req.getMinIntake());
            option.setMaxIntake(req.getMaxIntake());
            optionsToSave.add(option);
        }

        List<ElectiveGroupOption> savedOptions = optionRepository.saveAll(optionsToSave);
        return ResponseEntity.ok(savedOptions);
    }
    
    @GetMapping
    public List<ElectiveGroupOption> getAllOptions() {
        return optionRepository.findAll();
    }
}

package com.rspc.timetable.services;

import com.rspc.timetable.dto.ClassroomDTO;
import com.rspc.timetable.entities.Classroom;
import com.rspc.timetable.repositories.ClassroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassroomRepository classroomRepository;

    // Keep in sync with constraint provider roomTypeMatch()
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "LECTURE", "LECTURE_HALL", "CLASSROOM", "LAB", "TUTORIAL", "TUTORIAL_ROOM"
    );

    // ----- Entity endpoints -----
    public List<Classroom> getAllClassrooms() {
        return classroomRepository.findAll();
    }

    public Optional<Classroom> getClassroomById(Long id) {
        return classroomRepository.findById(id);
    }

    public Classroom saveClassroom(Classroom classroom) {
        normalize(classroom);
        validate(classroom);
        return classroomRepository.save(classroom);
    }

    public List<Classroom> saveAllClassrooms(List<Classroom> classrooms) {
        for (Classroom c : classrooms) {
            normalize(c);
            validate(c);
        }
        return classroomRepository.saveAll(classrooms);
    }

    public void deleteClassroom(Long id) {
        classroomRepository.deleteById(id);
    }

    // ----- DTO helpers -----
    public List<ClassroomDTO> getAllClassroomDTOs() {
        return classroomRepository.findAll()
                .stream()
                .map(ClassroomDTO::new)
                .collect(Collectors.toList());
    }

    public Optional<ClassroomDTO> getClassroomDTOById(Long id) {
        return classroomRepository.findById(id).map(ClassroomDTO::new);
    }

    // ----- helpers -----
    // Normalize no longer trims type because it’s enum
private void normalize(Classroom c) {
    if (c.getName() != null) {
        c.setName(c.getName().trim());
    }
    // No trimming or case conversion for enum type
}

// Validate checks if enum value is in allowed types
private void validate(Classroom c) {
    if (c.getName() == null || c.getName().isBlank()) {
        throw new IllegalArgumentException("Classroom name is required");
    }
    if (c.getType() == null || !ALLOWED_TYPES.contains(c.getType().name())) {
        throw new IllegalArgumentException("Invalid classroom type: " + (c.getType() != null ? c.getType().name() : "null"));
    }
}

}

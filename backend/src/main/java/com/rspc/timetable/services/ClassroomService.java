package com.rspc.timetable.services;

import com.rspc.timetable.dto.ClassroomDTO;
import com.rspc.timetable.entities.Classroom;
import com.rspc.timetable.repositories.ClassroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassroomRepository classroomRepository;

    // ----- Entity endpoints -----
    public List<Classroom> getAllClassrooms() {
        return classroomRepository.findAll();
    }

    public Optional<Classroom> getClassroomById(Long id) {
        return classroomRepository.findById(id);
    }

    public Classroom saveClassroom(Classroom classroom) {
        // Backward-compat if callers used roomNumber shim
        if (classroom.getName() == null) {
            try {
                String rn = classroom.getRoomNumber(); // shim in Classroom entity
                if (rn != null) classroom.setName(rn);
            } catch (NoSuchMethodError ignored) {}
        }
        return classroomRepository.save(classroom);
    }

    public List<Classroom> saveAllClassrooms(List<Classroom> classrooms) {
        for (Classroom c : classrooms) {
            if (c.getName() == null) {
                try {
                    String rn = c.getRoomNumber();
                    if (rn != null) c.setName(rn);
                } catch (NoSuchMethodError ignored) {}
            }
        }
        return classroomRepository.saveAll(classrooms);
    }

    public void deleteClassroom(Long id) {
        classroomRepository.deleteById(id);
    }

    // ----- DTO helpers (separate to avoid type mismatches) -----
    public List<ClassroomDTO> getAllClassroomDTOs() {
        return classroomRepository.findAll()
                .stream()
                .map(ClassroomDTO::new)
                .collect(Collectors.toList());
    }

    public Optional<ClassroomDTO> getClassroomDTOById(Long id) {
        return classroomRepository.findById(id).map(ClassroomDTO::new);
    }
}

package com.rspc.timetable.services;

import com.rspc.timetable.entities.Classroom;
import com.rspc.timetable.repositories.ClassroomRepository;
import com.rspc.timetable.dto.ClassroomDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassroomRepository classroomRepository;

    public List<Classroom> getAllClassrooms() {
        return classroomRepository.findAll();
    }

    public Optional<Classroom> getClassroomById(Long id) {
        return classroomRepository.findById(id);
    }

    public Classroom saveClassroom(Classroom classroom) {
        return classroomRepository.save(classroom);
    }

    public List<Classroom> saveAllClassrooms(List<Classroom> classrooms) {
        return classroomRepository.saveAll(classrooms);
    }

    public void deleteClassroom(Long id) {
        classroomRepository.deleteById(id);
    }

    /**
     * Correctly maps the updated Classroom entity to the ClassroomDTO.
     */
    public List<ClassroomDTO> getAllClassroomDTOs() {
        return classroomRepository.findAll()
                .stream()
                .map(ClassroomDTO::new) // Using the convenience constructor from the DTO
                .collect(Collectors.toList());
    }
}

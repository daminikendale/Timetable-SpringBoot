package com.rspc.timetable.services;

import com.rspc.timetable.entities.Classroom;
import com.rspc.timetable.repositories.ClassroomRepository;
import com.rspc.timetable.dto.ClassroomDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClassroomService {

    private final ClassroomRepository classroomRepository;

    public ClassroomService(ClassroomRepository classroomRepository) {
        this.classroomRepository = classroomRepository;
    }

    public List<Classroom> getAllClassrooms() {
        return classroomRepository.findAll();
    }

    // ✅ Return Optional here
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

    public List<ClassroomDTO> getAllClassroomDTOs() {
        return classroomRepository.findAll()
                .stream()
                .map(c -> new ClassroomDTO(c.getId(), c.getName(), c.getCapacity()))
                .collect(Collectors.toList());
    }
}

package com.rspc.timetable.services;

import com.rspc.timetable.entities.Teacher;
import com.rspc.timetable.repositories.TeacherRepository;
import com.rspc.timetable.dto.TeacherDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public Optional<Teacher> getTeacherById(Long id) {
        return teacherRepository.findById(id);
    }

    public Teacher saveTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    public List<Teacher> saveAllTeachers(List<Teacher> teachers) {
        return teacherRepository.saveAll(teachers);
    }

    public void deleteTeacher(Long id) {
        teacherRepository.deleteById(id);
    }

    // Entities -> DTOs
    public List<TeacherDTO> getAllTeacherDTOs() {
        return teacherRepository.findAll().stream()
                .map(t -> new TeacherDTO(
                        t.getId(),
                        t.getName(),
                        t.getEmail(),
                        t.getEmployeeId()
                ))
                .collect(Collectors.toList());
    }

    // DTOs -> Entities and save
    public List<Teacher> saveAllTeachersFromDTOs(List<TeacherDTO> teacherDTOs) {
        List<Teacher> teachers = teacherDTOs.stream()
                .map(dto -> new Teacher(dto.getName(), dto.getEmail(), dto.getEmployeeId()))
                .collect(Collectors.toList());

        return teacherRepository.saveAll(teachers);
    }
}

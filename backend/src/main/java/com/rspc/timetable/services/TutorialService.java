package com.rspc.timetable.services;

import com.rspc.timetable.entities.Tutorial;
import com.rspc.timetable.repositories.TutorialRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TutorialService {

    private final TutorialRepo tutorialRepo;

    public TutorialService(TutorialRepo tutorialRepo) {
        this.tutorialRepo = tutorialRepo;
    }

    public List<Tutorial> getAll() {
        return tutorialRepo.findAll();
    }

    public Optional<Tutorial> getById(Long id) {
        return tutorialRepo.findById(id);
    }

    public Tutorial save(Tutorial tutorial) {
        return tutorialRepo.save(tutorial);
    }

    public List<Tutorial> saveAll(List<Tutorial> tutorials) {
        return tutorialRepo.saveAll(tutorials);
    }

    public void delete(Long id) {
        tutorialRepo.deleteById(id);
    }
}

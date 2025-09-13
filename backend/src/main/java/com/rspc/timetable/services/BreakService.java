package com.rspc.timetable.services;

import com.rspc.timetable.entities.Break;
import com.rspc.timetable.repositories.BreakRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BreakService {

    private final BreakRepository breakRepository;

    public BreakService(BreakRepository breakRepository) {
        this.breakRepository = breakRepository;
    }

    public List<Break> saveAll(List<Break> breaks) {
        return breakRepository.saveAll(breaks);
    }

    public List<Break> getAll() {
        return breakRepository.findAll();
    }
}

package com.rspc.timetable.services;

import com.rspc.timetable.dto.BatchDTO;
import com.rspc.timetable.entities.Batch;
import com.rspc.timetable.entities.Division;
import com.rspc.timetable.repositories.BatchRepository;
import com.rspc.timetable.repositories.DivisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final BatchRepository batchRepo;
    private final DivisionRepository divisionRepo;

    @Transactional
    public BatchDTO create(BatchDTO dto) {
        Division div = divisionRepo.findById(dto.getDivisionId())
                .orElseThrow(() -> new IllegalArgumentException("Division not found: " + dto.getDivisionId()));

        Batch b = new Batch();
        b.setBatchName(dto.getBatchName());
        b.setSize(dto.getSize());
        b.setBatchNumber(dto.getBatchNumber());
        b.setDivision(div);

        b = batchRepo.save(b);
        return toDto(b);
    }

    @Transactional(readOnly = true)
    public List<BatchDTO> byDivision(Long divisionId) {
        return batchRepo.findByDivision_Id(divisionId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<BatchDTO> createBulk(List<BatchDTO> list) {
        return list.stream()
                .map(this::create)
                .collect(Collectors.toList());
    }

    private BatchDTO toDto(Batch b) {
        BatchDTO d = new BatchDTO();
        d.setId(b.getId());
        d.setBatchName(b.getBatchName());
        d.setBatchNumber(b.getBatchNumber());
        d.setSize(b.getSize());
        d.setDivisionId(b.getDivision() != null ? b.getDivision().getId() : null);
        return d;
    }
}

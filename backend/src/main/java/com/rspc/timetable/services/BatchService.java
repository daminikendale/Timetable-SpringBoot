package com.rspc.timetable.services;

import com.rspc.timetable.dto.BatchDTO;
import com.rspc.timetable.entities.Batch;
import com.rspc.timetable.entities.Division;
import com.rspc.timetable.repositories.BatchRepository;
import com.rspc.timetable.repositories.DivisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final BatchRepository batchRepo;
    private final DivisionRepository divisionRepo;

    // ✅ Create single batch
    public BatchDTO create(BatchDTO dto) {
        Division div = divisionRepo.findById(dto.getDivisionId())
                .orElseThrow(() -> new IllegalArgumentException("Division not found"));

        Batch b = new Batch();
        b.setBatchName(dto.getBatchName());  // ✅ changed field
        b.setSize(dto.getSize());
        b.setDivision(div);

        b = batchRepo.save(b);
        dto.setId(b.getId());
        return dto;
    }

    // ✅ Get all batches by division
    public List<BatchDTO> byDivision(Long divisionId) {
        return batchRepo.findByDivisionId(divisionId).stream().map(b -> {
            BatchDTO d = new BatchDTO();
            d.setId(b.getId());
            d.setBatchName(b.getBatchName());  // ✅ changed field
            d.setSize(b.getSize());
            d.setDivisionId(b.getDivision().getId());
            return d;
        }).toList();
    }

    // ✅ Bulk create batches
    public List<BatchDTO> createBulk(List<BatchDTO> list) {
        return list.stream().map(this::create).toList();
    }
}

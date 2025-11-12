package com.rspc.timetable.controllers;

import com.rspc.timetable.dto.BatchDTO;
import com.rspc.timetable.services.BatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/batches")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class BatchController {
  private final BatchService svc;

  @PostMapping
  public ResponseEntity<BatchDTO> create(@RequestBody BatchDTO dto) {
    return ResponseEntity.ok(svc.create(dto));
  }

  @PostMapping("/bulk")
  public ResponseEntity<List<BatchDTO>> createBulk(@RequestBody List<BatchDTO> dtos) {
    return ResponseEntity.ok(svc.createBulk(dtos));
  }

  @GetMapping("/by-division/{divisionId}")
  public ResponseEntity<List<BatchDTO>> byDivision(@PathVariable Long divisionId) {
    return ResponseEntity.ok(svc.byDivision(divisionId));
  }
}

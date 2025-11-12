package com.rspc.timetable.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "batches")

public class Batch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String batchName; // ✅ instead of 'name'
    private int size;

    @ManyToOne
    @JoinColumn(name = "division_id")
    private Division division;
}

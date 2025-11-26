package com.rspc.timetable.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "batches")
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String batchName;

    private Integer batchNumber;

    private Integer size;

    @ManyToOne
    @JoinColumn(name = "division_id")
    private Division division;
}

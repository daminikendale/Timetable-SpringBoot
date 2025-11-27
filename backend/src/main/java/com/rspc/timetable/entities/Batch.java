package com.rspc.timetable.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "batches")
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "batch_name", nullable = false)
    private String batchName;

    @Column(name = "batch_number", nullable = false)
    private Integer batchNumber;

    @Column(name = "size", nullable = false)
    private Integer size;

    @ManyToOne
    @JoinColumn(name = "division_id", nullable = false)
    private Division division;

    public Batch() { }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBatchName() { return batchName; }
    public void setBatchName(String batchName) { this.batchName = batchName; }

    public Integer getBatchNumber() { return batchNumber; }
    public void setBatchNumber(Integer batchNumber) { this.batchNumber = batchNumber; }

    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }

    public Division getDivision() { return division; }
    public void setDivision(Division division) { this.division = division; }
}

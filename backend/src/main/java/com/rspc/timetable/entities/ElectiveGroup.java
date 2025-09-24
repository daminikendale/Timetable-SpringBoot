package com.rspc.timetable.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "elective_groups")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ElectiveGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Semester semester;

    @Column(columnDefinition = "TEXT")
    private String notes;
}

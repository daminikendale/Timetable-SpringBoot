package com.rspc.timetable.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "elective_group_options")
@IdClass(ElectiveGroupOptionId.class)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ElectiveGroupOption {

    @Id
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ElectiveGroup electiveGroup;

    @Id
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Subject subject;

    private Integer minIntake;
    private Integer maxIntake;
}

package com.rspc.timetable.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ElectiveGroupOptionId implements Serializable {
    private Long electiveGroup;
    private Long subject;
}

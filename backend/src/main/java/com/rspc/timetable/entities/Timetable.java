package com.rspc.timetable.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "timetables")
public class Timetable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimetableStatus status;

    @Column(nullable = false)
    private boolean isValid = true;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime publishedAt;

    // --- Manual Getter and Setter for 'isValid' to prevent IDE errors ---
    public boolean isValid() {
        return this.isValid;
    }

    public void setValid(boolean valid) {
        this.isValid = valid;
    }
    // -------------------------------------------------------------------

    public enum TimetableStatus {
        DRAFT,
        PUBLISHED,
        ARCHIVED
    }
}

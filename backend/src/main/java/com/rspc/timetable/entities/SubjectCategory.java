// src/main/java/com/rspc/timetable/entities/SubjectCategory.java
package com.rspc.timetable.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SubjectCategory {
    REGULAR, PROGRAM_ELECTIVE, OPEN_ELECTIVE;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static SubjectCategory from(String value) {
        if (value == null) return null;
        return SubjectCategory.valueOf(value.trim().toUpperCase());
    }

    @JsonValue
    public String toJson() {
        return name();
    }
}

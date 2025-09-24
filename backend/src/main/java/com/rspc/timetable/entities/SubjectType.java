// src/main/java/com/rspc/timetable/entities/SubjectType.java
package com.rspc.timetable.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SubjectType {
    THEORY, LAB;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static SubjectType from(String value) {
        if (value == null) return null;
        return SubjectType.valueOf(value.trim().toUpperCase());
    }

    @JsonValue
    public String toJson() {
        return name();
    }
}

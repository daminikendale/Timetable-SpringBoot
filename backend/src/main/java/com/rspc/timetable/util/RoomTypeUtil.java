package com.rspc.timetable.util;

import com.rspc.timetable.entities.Classroom;

public final class RoomTypeUtil {
    private RoomTypeUtil() {}

    public static boolean isLecture(Classroom c) {
        return c != null && c.getType() != null && "LECTURE".equalsIgnoreCase(c.getType());
    }
    public static boolean isLab(Classroom c) {
        return c != null && c.getType() != null && "LAB".equalsIgnoreCase(c.getType());
    }
    public static boolean isTutorial(Classroom c) {
        return c != null && c.getType() != null && "TUTORIAL".equalsIgnoreCase(c.getType());
    }
}

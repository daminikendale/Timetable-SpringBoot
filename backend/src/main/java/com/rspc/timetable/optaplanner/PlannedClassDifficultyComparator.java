package com.rspc.timetable.optaplanner;

import com.rspc.timetable.entities.ScheduledClass;
import java.util.Comparator;
import java.util.Objects;

public class PlannedClassDifficultyComparator implements Comparator<PlannedClass> {

    @Override
    public int compare(PlannedClass a, PlannedClass b) {
        int byType = Integer.compare(rank(getSessionType(b)), rank(getSessionType(a)));
        if (byType != 0) return byType;

        boolean bHasBatch = hasBatch(b);
        boolean aHasBatch = hasBatch(a);
        int byScope = Boolean.compare(bHasBatch, aHasBatch);
        if (byScope != 0) return byScope;

        int byNulls = Integer.compare(nulls(b), nulls(a));
        if (byNulls != 0) return byNulls;

        Long aid = getScheduledId(a);
        Long bid = getScheduledId(b);
        if (!Objects.equals(aid, bid)) {
            if (aid == null) return 1;
            if (bid == null) return -1;
            return Long.compare(aid, bid);
        }
        return 0;
    }

    private int rank(Object sessionType) {
        if (sessionType == null) return 1;
        String name = sessionType.toString();
        if ("LAB".equalsIgnoreCase(name)) return 3;
        if ("TUTORIAL".equalsIgnoreCase(name)) return 2;
        return 1;
    }

    private int nulls(PlannedClass p) {
        int n = 0;
        if (getTeacher(p) == null) n++;
        if (p.getRoom() == null) n++;
        if (p.getTimeSlot() == null) n++;
        if (getDay(p) == null) n++;
        return n;
    }

    private Object getSessionType(PlannedClass p) {
        ScheduledClass sc = p.getScheduledClass();
        return sc != null ? sc.getSessionType() : null;
    }

    private boolean hasBatch(PlannedClass p) {
        ScheduledClass sc = p.getScheduledClass();
        return sc != null && sc.getBatch() != null;
    }

    private Long getScheduledId(PlannedClass p) {
        ScheduledClass sc = p.getScheduledClass();
        return sc != null ? sc.getId() : null;
    }

    private Object getTeacher(PlannedClass p) {
        ScheduledClass sc = p.getScheduledClass();
        return sc != null ? sc.getTeacher() : null;
    }

    private Object getDay(PlannedClass p) {
        ScheduledClass sc = p.getScheduledClass();
        return sc != null ? sc.getDayOfWeek() : null;
    }
}

package com.rspc.timetable.optaplanner;

import java.util.Comparator;
import java.util.Objects;

public class PlannedClassDifficultyComparator implements Comparator<PlannedClass> {

    @Override
    public int compare(PlannedClass a, PlannedClass b) {
        int byType = Integer.compare(rank(b.getSessionType()), rank(a.getSessionType()));
        if (byType != 0) return byType;

        boolean aHasBatch = a.getBatch() != null;
        boolean bHasBatch = b.getBatch() != null;
        int byScope = Boolean.compare(bHasBatch, aHasBatch);
        if (byScope != 0) return byScope;

        int byNulls = Integer.compare(nullCount(b), nullCount(a));
        if (byNulls != 0) return byNulls;

        Long aid = a.getId();
        Long bid = b.getId();
        if (!Objects.equals(aid, bid)) {
            if (aid == null) return 1;
            if (bid == null) return -1;
            return Long.compare(aid, bid);
        }
        return 0;
    }

    private int rank(String sessionType) {
        if (sessionType == null) return 1;
        switch (sessionType.toUpperCase()) {
            case "LAB": return 3;
            case "TUTORIAL": return 2;
            default: return 1;
        }
    }

    private int nullCount(PlannedClass p) {
        int n = 0;
        if (p.getTeacher() == null) n++;
        if (p.getRoom() == null) n++;
        if (p.getTimeSlot() == null) n++;
        return n;
    }
}

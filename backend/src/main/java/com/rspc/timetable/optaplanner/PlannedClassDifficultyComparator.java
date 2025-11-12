package com.rspc.timetable.optaplanner;

import com.rspc.timetable.entities.SessionType;
import java.util.Comparator;

public class PlannedClassDifficultyComparator implements Comparator<PlannedClass> {
    @Override
    public int compare(PlannedClass a, PlannedClass b) {
        int byLen = Integer.compare(b.getLengthSlots(), a.getLengthSlots());
        if (byLen != 0) return byLen;

        int byType = Integer.compare(rank(b.getSessionType()), rank(a.getSessionType()));
        if (byType != 0) return byType;

        int byScope = Boolean.compare(b.getBatch()!=null, a.getBatch()!=null);
        if (byScope != 0) return byScope;

        int byNulls = Integer.compare(nulls(b), nulls(a));
        if (byNulls != 0) return byNulls;

        long aid = a.getPlanningId()!=null ? a.getPlanningId() : -1L;
        long bid = b.getPlanningId()!=null ? b.getPlanningId() : -1L;
        return Long.compare(aid, bid);
    }

    private int rank(SessionType t) {
        if (t == SessionType.LAB) return 3;
        if (t == SessionType.TUTORIAL) return 2;
        return 1;
    }
    private int nulls(PlannedClass p) {
        int n=0;
        if (p.getTeacher()==null) n++;
        if (p.getRoom()==null) n++;
        if (p.getStartSlot()==null) n++;
        if (p.getDay()==null) n++;
        return n;
    }
}

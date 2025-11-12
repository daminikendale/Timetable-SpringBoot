package com.rspc.timetable.optaplanner;

import com.rspc.timetable.entities.SessionType;
import com.rspc.timetable.entities.ScheduledClass;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.*;

import java.time.Duration;
import java.time.LocalTime;

public class TimetableConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory f) {
        return new Constraint[] {
            teacherConflict(f),
            roomConflict(f),
            divisionConflict(f),
            forbidPinnedOverlap(f),
            roomTypeMatch(f),
            teacherUnavailable(f),
            perDayOnceDivision(f),
            perDayOnceBatch(f),
            minimizeDivisionIdle(f),
            preferEarlier(f)
        };
    }

    private Constraint teacherConflict(ConstraintFactory f) {
        return f.forEachUniquePair(PlannedClass.class,
                Joiners.equal(PlannedClass::getTeacher),
                Joiners.equal(PlannedClass::getDay),
                Joiners.filtering(this::overlap))
            .penalize("Teacher conflict", HardSoftScore.ONE_HARD);
    }

    private Constraint roomConflict(ConstraintFactory f) {
        return f.forEachUniquePair(PlannedClass.class,
                Joiners.equal(PlannedClass::getRoom),
                Joiners.equal(PlannedClass::getDay),
                Joiners.filtering(this::overlap))
            .penalize("Room conflict", HardSoftScore.ONE_HARD);
    }

    private Constraint divisionConflict(ConstraintFactory f) {
        return f.forEachUniquePair(PlannedClass.class,
                Joiners.equal(PlannedClass::getDivision),
                Joiners.equal(PlannedClass::getDay),
                Joiners.filtering(this::overlap))
            .penalize("Division conflict", HardSoftScore.ONE_HARD);
    }

    private Constraint forbidPinnedOverlap(ConstraintFactory f) {
        return f.forEach(PlannedClass.class)
            .filter(pc -> pc.getDay()!=null && pc.getStartSlot()!=null)
            .join(ScheduledClass.class,
                Joiners.equal(PlannedClass::getDivision, ScheduledClass::getDivision),
                Joiners.equal(PlannedClass::getDay, ScheduledClass::getDayOfWeek))
            .filter((pc, sc) ->
                (sc.getSessionType()==SessionType.SHORT_BREAK
              || sc.getSessionType()==SessionType.LUNCH)
              && timeOverlap(pc, sc))
            .penalize("Overlap with break/lunch", HardSoftScore.ONE_HARD);
    }

    private Constraint roomTypeMatch(ConstraintFactory f) {
        return f.forEach(PlannedClass.class)
            .filter(pc -> pc.getRoom()!=null && !isRoomValid(pc))
            .penalize("Room type mismatch", HardSoftScore.ONE_HARD);
    }

    private Constraint teacherUnavailable(ConstraintFactory f) {
        return f.forEach(PlannedClass.class)
            .filter(pc -> pc.getTeacher()!=null
                       && pc.getStartSlot()!=null
                       && pc.getTeacher().getUnavailableTimeSlots()!=null
                       && pc.getTeacher().getUnavailableTimeSlots().contains(pc.getStartSlot().getId()))
            .penalize("Teacher unavailable", HardSoftScore.ONE_HARD);
    }

    private Constraint perDayOnceDivision(ConstraintFactory f) {
        return f.forEach(PlannedClass.class)
            .filter(pc -> pc.getBatch()==null && pc.getDay()!=null)
            .groupBy(pc -> pc.getOffering().getId()+"-"+pc.getDivision().getId()+"-"+pc.getDay(),
                    ConstraintCollectors.count())
            .filter((key, cnt) -> cnt > 1)
            .penalize("Once per day (division)", HardSoftScore.ONE_HARD, (key, cnt) -> cnt - 1);
    }

    private Constraint perDayOnceBatch(ConstraintFactory f) {
        return f.forEach(PlannedClass.class)
            .filter(pc -> pc.getBatch()!=null && pc.getDay()!=null)
            .groupBy(pc -> pc.getOffering().getId()+"-"+pc.getBatch().getId()+"-"+pc.getDay(),
                    ConstraintCollectors.count())
            .filter((key, cnt) -> cnt > 1)
            .penalize("Once per day (batch)", HardSoftScore.ONE_HARD, (key, cnt) -> cnt - 1);
    }

    private Constraint minimizeDivisionIdle(ConstraintFactory f) {
        return f.forEach(PlannedClass.class)
            .filter(pc -> pc.getStartSlot()!=null && pc.getDay()!=null)
            .groupBy(pc -> pc.getDivision().getId()+"-"+pc.getDay().name(),
                    ConstraintCollectors.min((PlannedClass pc) -> pc.getStartSlot().getStartTime()),
                    ConstraintCollectors.max((PlannedClass pc) -> endTime(pc)),
                    ConstraintCollectors.count())
            .penalize("Division idle span", HardSoftScore.ONE_SOFT,
                (key, minStart, maxEnd, cnt) -> {
                    int spanTicks = (int)(Duration.between(minStart, maxEnd).toMinutes()/15) + 1;
                    return Math.max(0, spanTicks - cnt);
                });
    }

    private Constraint preferEarlier(ConstraintFactory f) {
        return f.forEach(PlannedClass.class)
            .filter(pc -> pc.getStartSlot()!=null)
            .penalize("Prefer earlier", HardSoftScore.ONE_SOFT,
                pc -> pc.getStartSlot().getStartTime().getHour()*4
                    + pc.getStartSlot().getStartTime().getMinute()/15);
    }

    private boolean isRoomValid(PlannedClass pc) {
        String type = pc.getRoom().getType();
        if (type == null) return false;
        String t = type.trim().toUpperCase();
        return switch (pc.getSessionType()) {
            case LAB -> t.equals("LAB");
            case TUTORIAL -> t.equals("TUTORIAL") || t.equals("TUTORIAL_ROOM")
                          || t.equals("LECTURE")  || t.equals("LECTURE_HALL") || t.equals("CLASSROOM");
            default -> t.equals("LECTURE") || t.equals("LECTURE_HALL") || t.equals("CLASSROOM");
        };
    }

    private boolean overlap(PlannedClass a, PlannedClass b) {
        if (a.getStartSlot()==null || b.getStartSlot()==null || a.getDay()==null || b.getDay()==null) return false;
        if (!a.getDay().equals(b.getDay())) return false;
        LocalTime aStart = a.getStartSlot().getStartTime();
        LocalTime aEnd   = endTime(a);
        LocalTime bStart = b.getStartSlot().getStartTime();
        LocalTime bEnd   = endTime(b);
        return aStart.isBefore(bEnd) && bStart.isBefore(aEnd);
    }

    private boolean timeOverlap(PlannedClass pc, ScheduledClass sc) {
        LocalTime aStart = pc.getStartSlot().getStartTime();
        LocalTime aEnd   = endTime(pc);
        LocalTime bStart = sc.getTimeSlot().getStartTime();
        LocalTime bEnd   = sc.getTimeSlot().getEndTime();
        return aStart.isBefore(bEnd) && bStart.isBefore(aEnd);
    }

    private LocalTime endTime(PlannedClass pc) {
        LocalTime end = pc.getStartSlot().getEndTime();
        if (pc.getLengthSlots()==2) {
            long dur = java.time.Duration.between(
                pc.getStartSlot().getStartTime(), pc.getStartSlot().getEndTime()
            ).toMinutes();
            end = end.plusMinutes(dur);
        }
        return end;
    }
}

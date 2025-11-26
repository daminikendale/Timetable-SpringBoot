package com.rspc.timetable.optaplanner;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.*;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;

import java.time.Duration;
import java.time.LocalTime;

public class TimetableConstraintProvider implements ConstraintProvider {
    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
                teacherConflict(factory),
                roomConflict(factory),
                noOverlapMultiSlot(factory)
        };
    }

    // If same teacher is assigned to two planned classes at same timeslot & day -> hard penalty
    private Constraint teacherConflict(ConstraintFactory factory) {
        return factory.from(PlannedClass.class)
                .filter(pc -> pc.getTeacher() != null && pc.getTimeSlot() != null && pc.getDay() != null)
                .join(PlannedClass.class,
                        Joiners.equal(PlannedClass::getTeacher),
                        Joiners.equal(PlannedClass::getTimeSlot),
                        Joiners.equal(PlannedClass::getDay),
                        Joiners.lessThan(PlannedClass::getId))
                .penalize("Teacher conflict", HardSoftScore.ofHard(1));
    }

    // Room conflict (exact same slot)
    private Constraint roomConflict(ConstraintFactory factory) {
        return factory.from(PlannedClass.class)
            .filter(pc -> pc.getRoom() != null && pc.getTimeSlot() != null && pc.getDay() != null)
            .join(PlannedClass.class,
                    Joiners.equal(PlannedClass::getRoom),
                    Joiners.equal(PlannedClass::getTimeSlot),
                    Joiners.equal(PlannedClass::getDay),
                    Joiners.lessThan(PlannedClass::getId))
            .penalize("Room conflict", HardSoftScore.ofHard(1));
    }

    // Detect overlapping intervals when at least one planned class uses multiple slots.
    // Uses TimeSlot.startTime & TimeSlot.endTime and slotCount to compute an end instant.
    private Constraint noOverlapMultiSlot(ConstraintFactory factory) {
        return factory.from(PlannedClass.class)
                .filter(pc -> pc.getTimeSlot() != null && pc.getDay() != null && pc.getSlotCount() > 0)
                .join(PlannedClass.class,
                        Joiners.equal(PlannedClass::getDay),
                        Joiners.lessThan(PlannedClass::getId))
                .filter((a, b) -> {
                    if (a.getTimeSlot() == null || b.getTimeSlot() == null) return false;
                    // only care if at least one is multi-slot (so we catch multi vs single or multi vs multi)
                    if (!a.isMultiSlot() && !b.isMultiSlot()) return false;

                    LocalTime aStart = a.getTimeSlot().getStartTime();
                    LocalTime bStart = b.getTimeSlot().getStartTime();
                    Duration aSlotDur = Duration.between(a.getTimeSlot().getStartTime(), a.getTimeSlot().getEndTime());
                    Duration bSlotDur = Duration.between(b.getTimeSlot().getStartTime(), b.getTimeSlot().getEndTime());

                    Duration aTotal = aSlotDur.multipliedBy(a.getSlotCount());
                    Duration bTotal = bSlotDur.multipliedBy(b.getSlotCount());

                    LocalTime aEnd = aStart.plus(aTotal);
                    LocalTime bEnd = bStart.plus(bTotal);

                    // overlap if intervals intersect: aStart < bEnd && bStart < aEnd
                    return aStart.isBefore(bEnd) && bStart.isBefore(aEnd);
                })
                .penalize("Multi-slot overlap", HardSoftScore.ofHard(1));
    }
}

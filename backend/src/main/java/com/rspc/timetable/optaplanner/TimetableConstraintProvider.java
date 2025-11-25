package com.rspc.timetable.optaplanner;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.*;
import com.rspc.timetable.optaplanner.PlannedClass;

import static org.optaplanner.core.api.score.stream.Joiners.*;

public class TimetableConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[]{
                requiredTimeSlot(factory),
                requiredRoom(factory),
                roomConflict(factory),
                teacherConflict(factory),
                divisionConflict(factory)
        };
    }

    private Constraint requiredTimeSlot(ConstraintFactory factory) {
        // penalize plannedClasses that do not have an assigned time slot
        return factory.forEach(PlannedClass.class)
                .filter(pc -> pc.getTimeSlot() == null)
                .penalize("requiredTimeSlot", HardSoftScore.ONE_HARD);
    }

    private Constraint requiredRoom(ConstraintFactory factory) {
        // penalize plannedClasses that do not have an assigned room
        return factory.forEach(PlannedClass.class)
                .filter(pc -> pc.getRoom() == null)
                .penalize("requiredRoom", HardSoftScore.ONE_HARD);
    }

    private Constraint roomConflict(ConstraintFactory factory) {
        // Two planned classes at same time in same room -> hard penalty
        return factory.forEachUniquePair(PlannedClass.class,
                        equal(PlannedClass::getTimeSlot),
                        equal(PlannedClass::getRoom))
                .penalize("Room conflict", HardSoftScore.ONE_HARD);
    }

    private Constraint teacherConflict(ConstraintFactory factory) {
        // Two planned classes at same time for same teacher -> hard penalty
        return factory.forEachUniquePair(PlannedClass.class,
                        equal(pc -> pc.getScheduledClass().getTeacher()),
                        equal(PlannedClass::getTimeSlot))
                .penalize("Teacher conflict", HardSoftScore.ONE_HARD);
    }

    private Constraint divisionConflict(ConstraintFactory factory) {
        // Two planned classes at same time for same division (batch) -> hard penalty
        return factory.forEachUniquePair(PlannedClass.class,
                        equal(pc -> pc.getScheduledClass().getDivision()),
                        equal(PlannedClass::getTimeSlot))
                .penalize("Division conflict", HardSoftScore.ONE_HARD);
    }
}

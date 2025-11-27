package com.rspc.timetable.optaplanner;

import org.optaplanner.core.api.score.stream.*;
import com.rspc.timetable.entities.*;
import org.springframework.stereotype.Component;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

@Component
public class TimetableConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[]{
                roomConflict(factory),
                teacherConflict(factory),
                divisionConflict(factory)
        };
    }

    private Constraint roomConflict(ConstraintFactory factory) {
        return factory.forEachUniquePair(PlannedClass.class,
                        Joiners.equal(PlannedClass::getRoom),
                        Joiners.equal(PlannedClass::getTimeSlot))
                .penalize("Room double booking", HardSoftScore.ONE_HARD);
    }

    private Constraint teacherConflict(ConstraintFactory factory) {
        return factory.forEachUniquePair(PlannedClass.class,
                        Joiners.equal(PlannedClass::getTeacher),
                        Joiners.equal(PlannedClass::getTimeSlot))
                .penalize("Teacher conflict", HardSoftScore.ONE_HARD);
    }

    private Constraint divisionConflict(ConstraintFactory factory) {
        return factory.forEachUniquePair(PlannedClass.class,
                        Joiners.equal(PlannedClass::getDivision),
                        Joiners.equal(PlannedClass::getTimeSlot))
                .penalize("Division conflict", HardSoftScore.ONE_HARD);
    }
}

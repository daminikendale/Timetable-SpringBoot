package com.rspc.timetable.optaplanner;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;

public class TimetableConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[]{
                teacherConflict(factory),
                roomConflict(factory),
                divisionConflict(factory),
                batchConflict(factory),
                multiSlotAlignment(factory),
                teacherAvailability(factory),
                preferHighPriorityTeacher(factory)
        };
    }

    // ------------------- HARD CONSTRAINTS -------------------

    private Constraint teacherConflict(ConstraintFactory factory) {
        return factory.from(PlannedClass.class)
                .filter(pc -> pc.getTeacher() != null)
                .join(PlannedClass.class,
                        org.optaplanner.core.api.score.stream.Joiners.equal(PlannedClass::getTeacher),
                        org.optaplanner.core.api.score.stream.Joiners.equal(PlannedClass::getDay),
                        org.optaplanner.core.api.score.stream.Joiners.equal(PlannedClass::getTimeSlot),
                        org.optaplanner.core.api.score.stream.Joiners.greaterThan(PlannedClass::getId)
                )
                .penalize("Teacher conflict", HardSoftScore.ONE_HARD);
    }

    private Constraint roomConflict(ConstraintFactory factory) {
        return factory.from(PlannedClass.class)
                .filter(pc -> pc.getRoom() != null)
                .join(PlannedClass.class,
                        org.optaplanner.core.api.score.stream.Joiners.equal(PlannedClass::getRoom),
                        org.optaplanner.core.api.score.stream.Joiners.equal(PlannedClass::getDay),
                        org.optaplanner.core.api.score.stream.Joiners.equal(PlannedClass::getTimeSlot),
                        org.optaplanner.core.api.score.stream.Joiners.greaterThan(PlannedClass::getId)
                )
                .penalize("Room conflict", HardSoftScore.ONE_HARD);
    }

    private Constraint divisionConflict(ConstraintFactory factory) {
        return factory.from(PlannedClass.class)
                .join(PlannedClass.class,
                        org.optaplanner.core.api.score.stream.Joiners.equal(PlannedClass::getDivision),
                        org.optaplanner.core.api.score.stream.Joiners.equal(PlannedClass::getDay),
                        org.optaplanner.core.api.score.stream.Joiners.equal(PlannedClass::getTimeSlot),
                        org.optaplanner.core.api.score.stream.Joiners.greaterThan(PlannedClass::getId)
                )
                .penalize("Division conflict", HardSoftScore.ONE_HARD);
    }

    private Constraint batchConflict(ConstraintFactory factory) {
        return factory.from(PlannedClass.class)
                .filter(pc -> pc.getBatch() != null)
                .join(PlannedClass.class,
                        org.optaplanner.core.api.score.stream.Joiners.equal(PlannedClass::getBatch),
                        org.optaplanner.core.api.score.stream.Joiners.equal(PlannedClass::getDay),
                        org.optaplanner.core.api.score.stream.Joiners.equal(PlannedClass::getTimeSlot),
                        org.optaplanner.core.api.score.stream.Joiners.greaterThan(PlannedClass::getId)
                )
                .penalize("Batch conflict", HardSoftScore.ONE_HARD);
    }

    private Constraint multiSlotAlignment(ConstraintFactory factory) {
        return factory.from(PlannedClass.class)
                .filter(pc -> pc.isMultiSlot())
                .filter(pc -> pc.getTimeSlot() == null)
                .penalize("Lab must occupy continuous slots", HardSoftScore.ONE_HARD);
    }

    private Constraint teacherAvailability(ConstraintFactory factory) {
        return factory.from(PlannedClass.class)
            .filter(pc -> pc.getTeacher() != null)
            .filter(pc -> pc.getEligibleTeachers() != null && !pc.getEligibleTeachers().contains(pc.getTeacher()))
            .penalize("Assigned teacher not qualified", HardSoftScore.ONE_HARD);
    }

    // ------------------- SOFT CONSTRAINTS -------------------

    private Constraint preferHighPriorityTeacher(ConstraintFactory factory) {
        return factory.from(PlannedClass.class)
                .filter(pc -> pc.getTeacher() != null)
                .reward("Higher priority teacher", HardSoftScore.ONE_SOFT);
    }
}

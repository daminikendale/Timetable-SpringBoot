package com.rspc.timetable.optaplanner;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.*;

public class TimetableConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[]{
                roomConflict(factory),
                teacherOverlap(factory),
                sameDivisionSameSubjectSameTeacher(factory)
        };
    }

    // HARD: 2 classes cannot be in same room at same time
    private Constraint roomConflict(ConstraintFactory factory) {
        return factory.forEach(PlannedClass.class)
                .filter(pc -> pc.getRoom() != null && pc.getTimeSlot() != null)
                .join(PlannedClass.class,
                        Joiners.equal(PlannedClass::getRoom),
                        Joiners.equal(PlannedClass::getTimeSlot),
                        Joiners.lessThan(PlannedClass::getId))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Room conflict");
    }

    // HARD: teacher cannot teach 2 classes at same time
    private Constraint teacherOverlap(ConstraintFactory factory) {
        return factory.forEach(PlannedClass.class)
                .filter(pc -> pc.getTimeSlot() != null && pc.getTeacher() != null)
                .join(PlannedClass.class,
                        Joiners.equal(PlannedClass::getTeacher),
                        Joiners.equal(PlannedClass::getTimeSlot),
                        Joiners.lessThan(PlannedClass::getId))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Teacher overlap");
    }

    // HARD: For a given division+subject -> same teacher must teach all lectures
    private Constraint sameDivisionSameSubjectSameTeacher(ConstraintFactory factory) {
        return factory.forEach(PlannedClass.class)
                .filter(PlannedClass::isLecture)
                .join(PlannedClass.class,
                        Joiners.equal(PlannedClass::getDivision),
                        Joiners.equal(PlannedClass::getSubject),
                        Joiners.lessThan(PlannedClass::getId))
                .filter((a, b) -> 
                        a.getTeacher() != null &&
                        b.getTeacher() != null &&
                        !a.getTeacher().getId().equals(b.getTeacher().getId()))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Lecture must have consistent teacher per subject per division");
    }
}

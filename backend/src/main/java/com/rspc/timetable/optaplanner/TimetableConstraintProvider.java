package com.rspc.timetable.optaplanner;

import com.rspc.timetable.entities.*;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.*;

public class TimetableConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
                roomConflict(factory),
                teacherOverlap(factory),
                lectureDivisionSubjectSameTeacher(factory)
        };
    }

    // two classes cannot be in the same room at same time (hard)
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

    // teacher cannot teach two classes at same time (hard)
    private Constraint teacherOverlap(ConstraintFactory factory) {
        return factory.forEach(PlannedClass.class)
                .filter(pc -> pc.getTeacher() != null && pc.getTimeSlot() != null)
                .join(PlannedClass.class,
                        Joiners.equal(PlannedClass::getTeacher),
                        Joiners.equal(PlannedClass::getTimeSlot),
                        Joiners.lessThan(PlannedClass::getId))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Teacher overlap");
    }

    // For a given division + subject, prefer same teacher for lectures (hard)
    private Constraint lectureDivisionSubjectSameTeacher(ConstraintFactory factory) {
        return factory.forEach(PlannedClass.class)
                .filter(PlannedClass::isLecture)
                .join(PlannedClass.class,
                        Joiners.equal(PlannedClass::getDivision),
                        Joiners.equal(PlannedClass::getSubject),
                        Joiners.lessThan(PlannedClass::getId))
                .filter((a, b) -> a.getTeacher() != null && b.getTeacher() != null
                        && !a.getTeacher().getId().equals(b.getTeacher().getId()))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Lecture same teacher per division/subject");
    }
}

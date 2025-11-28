package com.rspc.timetable.optaplanner;

import com.rspc.timetable.entities.ScheduledClass;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;

public class TimetableConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[]{
                roomConflict(factory),
                teacherConflict(factory),
                studentConflict(factory),
                teacherContinuousHoursLimit(factory)
        };
    }

    // -----------------------------------------
    // Hard Constraints
    // -----------------------------------------

    // 1. ROOM CONFLICT
    private Constraint roomConflict(ConstraintFactory factory) {
        return factory.forEach(ScheduledClass.class)
                .join(ScheduledClass.class,
                        org.optaplanner.core.api.score.stream.Joiners.equal(ScheduledClass::getClassroom),
                        org.optaplanner.core.api.score.stream.Joiners.equal(sc -> sc.getTimeSlot().getId()),
                        org.optaplanner.core.api.score.stream.Joiners.lessThan(ScheduledClass::getId)
                )
                .penalize("Room conflict", HardSoftScore.ONE_HARD);
    }

    // 2. TEACHER CONFLICT
    private Constraint teacherConflict(ConstraintFactory factory) {
        return factory.forEach(ScheduledClass.class)
                .filter(sc -> sc.getTeacher() != null)
                .join(ScheduledClass.class,
                        org.optaplanner.core.api.score.stream.Joiners.equal(sc -> sc.getTeacher().getId()),
                        org.optaplanner.core.api.score.stream.Joiners.equal(sc -> sc.getTimeSlot().getId()),
                        org.optaplanner.core.api.score.stream.Joiners.lessThan(ScheduledClass::getId)
                )
                .penalize("Teacher conflict", HardSoftScore.ONE_HARD);
    }

    // 3. DIVISION/STUDENT (same division cannot have two classes at same time)
    private Constraint studentConflict(ConstraintFactory factory) {
        return factory.forEach(ScheduledClass.class)
                .join(ScheduledClass.class,
                        org.optaplanner.core.api.score.stream.Joiners.equal(sc -> sc.getDivision().getId()),
                        org.optaplanner.core.api.score.stream.Joiners.equal(sc -> sc.getTimeSlot().getId()),
                        org.optaplanner.core.api.score.stream.Joiners.lessThan(ScheduledClass::getId)
                )
                .penalize("Student conflict", HardSoftScore.ONE_HARD);
    }

    // 4. TEACHER continuous hours limit (max 4 hours)
    private Constraint teacherContinuousHoursLimit(ConstraintFactory factory) {
        return factory.forEach(ScheduledClass.class)
                .filter(sc -> sc.getTeacher() != null)
                .groupBy(sc -> sc.getTeacher().getId(),
                        org.optaplanner.core.api.score.stream.ConstraintCollectors.count())
                .filter((teacherId, classCount) -> classCount > 4)
                .penalize("Teacher continuous hours > 4",
                        HardSoftScore.ONE_HARD,
                        (teacherId, classCount) -> classCount - 4);
    }
}

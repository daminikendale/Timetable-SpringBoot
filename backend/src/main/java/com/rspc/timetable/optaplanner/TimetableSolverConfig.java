package com.rspc.timetable.optaplanner;

import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class TimetableSolverConfig {

    @Bean
    public SolverConfig solverConfig() {
        return new SolverConfig()
                .withSolutionClass(TimetableSolution.class)
                .withEntityClasses(PlannedClass.class)
                .withScoreDirectorFactory(
                        new ScoreDirectorFactoryConfig()
                                .withConstraintProviderClass(TimetableConstraintProvider.class)
                )
                .withTerminationSpentLimit(Duration.ofSeconds(20));
    }
}

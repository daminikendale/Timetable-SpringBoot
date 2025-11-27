package com.rspc.timetable.config;

import com.rspc.timetable.optaplanner.PlannedClass;
import com.rspc.timetable.optaplanner.TimetableConstraintProvider;
import com.rspc.timetable.optaplanner.TimetableSolution;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

@Configuration
public class OptaPlannerConfig {

    @Bean
    public SolverFactory<TimetableSolution> solverFactory() {
        SolverConfig solverConfig = new SolverConfig()
                .withSolutionClass(TimetableSolution.class)
                .withEntityClassList(List.of(PlannedClass.class))
                .withConstraintProviderClass(TimetableConstraintProvider.class)
                .withTerminationConfig(
                        new TerminationConfig()
                                .withSpentLimit(Duration.ofSeconds(20))
                );
        return SolverFactory.create(solverConfig);
    }

    @Bean
    public SolverManager<TimetableSolution, Long> solverManager(
            SolverFactory<TimetableSolution> solverFactory
    ) {
        return SolverManager.create(solverFactory);
    }
}

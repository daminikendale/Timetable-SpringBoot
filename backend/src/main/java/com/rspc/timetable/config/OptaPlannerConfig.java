package com.rspc.timetable.config;

import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.api.solver.SolverFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.rspc.timetable.optaplanner.TimetableSolution;

@Configuration
public class OptaPlannerConfig {

    @Bean
    public SolverFactory<TimetableSolution> solverFactory() {
        return SolverFactory.createFromXmlResource("solver/solverConfig.xml");
    }

    @Bean
    public SolverManager<TimetableSolution, Long> solverManager(
            SolverFactory<TimetableSolution> solverFactory) {
        return SolverManager.create(solverFactory);
    }
}

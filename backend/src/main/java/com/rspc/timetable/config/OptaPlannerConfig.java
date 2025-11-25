package com.rspc.timetable.config;

import com.rspc.timetable.optaplanner.TimetableConstraintProvider;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OptaPlannerConfig {

    @Bean
    public ConstraintProvider constraintProvider() {
        return new TimetableConstraintProvider();
    }
}

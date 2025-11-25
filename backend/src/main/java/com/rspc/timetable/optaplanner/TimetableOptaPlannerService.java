package com.rspc.timetable.optaplanner;

import lombok.extern.slf4j.Slf4j;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.api.solver.SolverStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

@Slf4j
@Service
public class TimetableOptaPlannerService {

    private final SolverManager<TimetableSolution, Long> solverManager;
    private final TimetableProblemLoader problemLoader;

    private final Map<Long, SolverJob<TimetableSolution, Long>> jobs = new ConcurrentHashMap<>();

    public TimetableOptaPlannerService(SolverManager<TimetableSolution, Long> solverManager,
                                       TimetableProblemLoader problemLoader) {
        this.solverManager = solverManager;
        this.problemLoader = problemLoader;
    }

    public Long startSolving(Long semesterId) {
        if (jobs.containsKey(semesterId)) {
            log.info("Solver already running for semester {}", semesterId);
            return semesterId;
        }
        Function<Long, TimetableSolution> problemFinder = problemLoader::loadProblemForSemester;
        SolverJob<TimetableSolution, Long> job = solverManager.solveAndListen(
            semesterId,
            problemFinder,
            solution -> log.info("New best solution for semester {} with score {}", semesterId, solution.getScore())
        );
        jobs.put(semesterId, job);
        return semesterId;
    }

    public String getStatus(Long semesterId) {
        SolverJob<TimetableSolution, Long> job = jobs.get(semesterId);
        if (job == null) {
            return "NOT_SOLVING";
        }
        SolverStatus status = job.getSolverStatus();
        return status == null ? "UNKNOWN" : status.name();
    }

    public Optional<TimetableSolution> getResult(Long semesterId) {
        SolverJob<TimetableSolution, Long> job = jobs.get(semesterId);
        if (job == null) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(job.getFinalBestSolution());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted getting solution", e);
            return Optional.empty();
        } catch (ExecutionException e) {
            log.error("Error getting solution", e);
            return Optional.empty();
        }
    }

    public boolean terminate(Long semesterId) {
        SolverJob<TimetableSolution, Long> job = jobs.remove(semesterId);
        if (job == null) {
            return false;
        }
        job.terminateEarly();
        log.info("Solver terminated for semester {}", semesterId);
        return true;
    }
}

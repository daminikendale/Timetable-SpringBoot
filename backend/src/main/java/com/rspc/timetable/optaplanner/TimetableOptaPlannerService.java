package com.rspc.timetable.optaplanner;

import lombok.RequiredArgsConstructor;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.api.solver.SolverStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
public class TimetableOptaPlannerService {

    private final SolverManager<TimetableSolution, Long> solverManager;
    private final SolverFactory<TimetableSolution> solverFactory;
    private final TimetableProblemLoader problemLoader;

    // Store async solutions
    private final ConcurrentMap<Long, TimetableSolution> solutionStore = new ConcurrentHashMap<>();

    /**
     * -------------- 1. SYNCHRONOUS SOLVING (Controller /run/{id}) --------------
     */
    public TimetableSolution solve(TimetableSolution problem) {
        Solver<TimetableSolution> solver = solverFactory.buildSolver();
        return solver.solve(problem);
    }

    /**
     * -------------- 2. ASYNC SOLVER (Controller /solve/{id}) --------------
     */
    public Long startSolving(Long semesterId) {

    solverManager.solve(
            semesterId,                     // Problem ID
            id -> loadProblem(id),          // Problem loader (Function<Long, TimetableSolution>)
            solution -> solutionStore.put(semesterId, solution)  // Only receives solution
    );

    return semesterId;  // job ID
}


    private TimetableSolution loadProblem(Long semId) {
        return problemLoader.loadProblemForSemester(semId);
    }

    /**
     * -------------- 3. CHECK STATUS --------------
     */
    public String getStatus(Long semesterId) {
        SolverStatus status = solverManager.getSolverStatus(semesterId);
        return status == null ? "NOT_STARTED" : status.name();
    }

    /**
     * -------------- 4. GET FINAL RESULT --------------
     */
    public Optional<TimetableSolution> getResult(Long semesterId) {
        return Optional.ofNullable(solutionStore.get(semesterId));
    }

    /**
     * -------------- 5. TERMINATE --------------
     */
    public boolean terminate(Long semesterId) {
        try {
            solverManager.terminateEarly(semesterId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

package com.rspc.timetable.optaplanner;

import com.rspc.timetable.services.TimeTableProblemService;
import lombok.extern.slf4j.Slf4j;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.SolverManager;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class TimetableOptaPlannerService {

    private final SolverManager<TimetableSolution, Long> solverManager;
    private final TimeTableProblemService problemService;
    private final Map<Long, TimetableSolution> resultMap = new ConcurrentHashMap<>();

    public TimetableOptaPlannerService(
            SolverManager<TimetableSolution, Long> solverManager,
            TimeTableProblemService problemService
    ) {
        this.solverManager = solverManager;
        this.problemService = problemService;
    }

    // ----------- SYNC SOLVING --------------
    public TimetableSolution solveForSemester(Long semesterId) {
        TimetableSolution unsolved = problemService.load(semesterId);

        SolverFactory<TimetableSolution> solverFactory =
                SolverFactory.createFromXmlResource("solver/timetableSolverConfig.xml");
        Solver<TimetableSolution> solver = solverFactory.buildSolver();

        TimetableSolution solved = solver.solve(unsolved);
        problemService.saveSolution(solved, semesterId);
        return solved;
    }

    // ----------- ASYNC SOLVING --------------
    public Long startSolving(Long semesterId) {
        TimetableSolution problem = problemService.load(semesterId);

        solverManager.solve(semesterId, problem, finalBest -> {
            resultMap.put(semesterId, finalBest);
            problemService.saveSolution(finalBest, semesterId);
        });
        return semesterId;
    }

    // ----------- STATUS --------------
    public String getStatus(Long semesterId) {
        return solverManager.getSolverStatus(semesterId).name();
    }

    // ----------- GET RESULT --------------
    public Optional<TimetableSolution> getResult(Long semesterId) {
        return Optional.ofNullable(resultMap.get(semesterId));
    }

    // ----------- STOP --------------
    public boolean terminate(Long semesterId) {
        solverManager.terminateEarly(semesterId);
        return true;
    }
}

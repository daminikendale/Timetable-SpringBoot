package com.rspc.timetable.optaplanner;

import com.rspc.timetable.services.TimeTableProblemService;
import lombok.extern.slf4j.Slf4j;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.api.solver.SolverStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class TimetableOptaPlannerService {

    private final SolverManager<TimetableSolution, Long> solverManager;
    private final TimetableProblemLoader problemLoader;
    private final TimeTableProblemService problemService;  // ✅ FIXED: injected

    private final Map<Long, TimetableSolution> resultMap = new ConcurrentHashMap<>();
    private final Map<Long, String> statusMap = new ConcurrentHashMap<>();

    public TimetableOptaPlannerService(
            SolverManager<TimetableSolution, Long> solverManager,
            TimetableProblemLoader problemLoader,
            TimeTableProblemService problemService   // <-- FIXED
    ) {
        this.solverManager = solverManager;
        this.problemLoader = problemLoader;
        this.problemService = problemService;
    }

    public Long startSolving(Long semesterId) {

        statusMap.put(semesterId, "SOLVING");

        TimetableSolution problem = problemLoader.loadProblemForSemester(semesterId);

        solverManager.solve(
                semesterId,
                problem,
                solvedSolution -> {
                    log.info("Solver finished for semester {}", semesterId);

                    resultMap.put(semesterId, solvedSolution);
                    statusMap.put(semesterId, "COMPLETED");

                    // SAVE TO DATABASE
                    problemService.saveSolution(solvedSolution, semesterId);
                }
        );

        return semesterId;
    }

    public String getStatus(Long semesterId) {
        SolverStatus status = solverManager.getSolverStatus(semesterId);
        if (status == SolverStatus.NOT_SOLVING) {
            return statusMap.getOrDefault(semesterId, "NOT_SOLVING");
        }
        return status.toString();
    }

    public Optional<TimetableSolution> getResult(Long semesterId) {
        return Optional.ofNullable(resultMap.get(semesterId));
    }

    public boolean terminate(Long semesterId) {
        SolverStatus status = solverManager.getSolverStatus(semesterId);
        if (status == SolverStatus.NOT_SOLVING) return false;

        solverManager.terminateEarly(semesterId);
        statusMap.put(semesterId, "TERMINATED");
        return true;
    }
}

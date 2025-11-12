package com.rspc.timetable.controllers;

import com.rspc.timetable.dto.CreateScheduledClassRequest;
import com.rspc.timetable.dto.ScheduledClassDTO;
import com.rspc.timetable.services.ScheduledClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ScheduledClassController {

    private final ScheduledClassService scheduledClassService;
    private final JdbcTemplate jdbcTemplate;

    // =============================
    // 🧩 Existing Timetable Endpoints
    // =============================
    @GetMapping("/timetable/by-division/{divisionId}")
    public ResponseEntity<List<ScheduledClassDTO>> getTimetableForDivision(@PathVariable("divisionId") Long divisionId) {
        return ResponseEntity.ok(scheduledClassService.getTimetableForDivision(divisionId));
    }

    @DeleteMapping("/scheduled-classes/by-division/{divisionId}")
    public ResponseEntity<Void> deleteByDivision(@PathVariable Long divisionId) {
        scheduledClassService.deleteByDivision(divisionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/scheduled-classes")
    public ResponseEntity<Map<String, Object>> createScheduledClass(@RequestBody CreateScheduledClassRequest request) {
        String sql = """
            INSERT INTO scheduled_classes (
                day_of_week, 
                session_type, 
                division_id, 
                time_slot_id,       -- ✅ fixed here too
                course_offering_id, 
                teacher_id, 
                classroom_id
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        jdbcTemplate.update(sql,
            request.getDayOfWeek(),
            request.getSessionType(),
            request.getDivisionId(),
            request.getTimeslotId(),
            request.getCourseOfferingId(),
            request.getTeacherId(),
            request.getClassroomId()
        );

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Class scheduled successfully!"
        ));
    }

    @GetMapping("/timetable/by-teacher/{teacherId}")
    public ResponseEntity<List<ScheduledClassDTO>> getTimetableForTeacher(@PathVariable("teacherId") Long teacherId) {
        return ResponseEntity.ok(scheduledClassService.getTimetableForTeacher(teacherId));
    }

    // =============================
    // ✨ Division Timetable for Student Grid
    // =============================
    @GetMapping("/scheduled-classes/division/{divisionId}/timetable")
    public ResponseEntity<List<Map<String, Object>>> getTimetableGrid(@PathVariable Long divisionId) {
        String sql = """
            SELECT 
                sc.id,
                sc.day_of_week,
                ts.start_time,
                ts.end_time,
                s.code AS subject_code,
                s.name AS subject_name,
                t.name AS teacher_name,
                c.room_number AS classroom,
                sc.session_type
            FROM scheduled_classes sc
            JOIN timeslots ts ON sc.time_slot_id = ts.id    -- ✅ fixed here
            JOIN course_offerings co ON sc.course_offering_id = co.id
            JOIN subjects s ON co.subject_id = s.id
            JOIN teachers t ON sc.teacher_id = t.id
            LEFT JOIN classrooms c ON sc.classroom_id = c.id
            WHERE sc.division_id = ?
            ORDER BY 
                FIELD(sc.day_of_week, 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY'),
                ts.start_time
            """;

        List<Map<String, Object>> timetable = jdbcTemplate.queryForList(sql, divisionId);
        return ResponseEntity.ok(timetable);
    }
}

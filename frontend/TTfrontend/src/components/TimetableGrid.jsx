// TimetableGrid.js
import React, { useEffect, useState } from "react";
import axios from "axios";

export default function TimetableGrid({ divisionId }) {
  const [timetable, setTimetable] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [timeSlots, setTimeSlots] = useState([]);

  const days = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"];

  // helper to read startTime/endTime robustly
  const extractStart = (ts) => ts?.startTime ?? ts?.start_time ?? null;
  const extractEnd = (ts) => ts?.endTime ?? ts?.end_time ?? null;

  // Load timeslots sorted
  useEffect(() => {
    axios
      .get("http://localhost:8080/api/timeslots")
      .then((res) => {
        const arr = (res.data || []).map((ts) => ({
          id: ts.id,
          startTime: (extractStart(ts) || "").toString(),
          endTime: (extractEnd(ts) || "").toString(),
          raw: ts,
        }));
        arr.sort((a, b) => a.startTime.localeCompare(b.startTime));
        setTimeSlots(arr);
      })
      .catch(() => setError("Failed to load timeslots"));
  }, []);

  // Load scheduled classes
  useEffect(() => {
    if (!divisionId) return;
    setLoading(true);

    axios
      .get(`http://localhost:8080/api/scheduled-classes/division/${divisionId}/timetable`)
      .then((res) => {
        setTimetable(res.data || []);
        setError("");
      })
      .catch(() => setError("Failed to load timetable"))
      .finally(() => setLoading(false));
  }, [divisionId]);

  // Build grid structure: grid[DAY][start] = [entries]
  const grid = {};
  (timetable || []).forEach((rowRaw) => {
    const row = {
      ...rowRaw,
      dayOfWeek: rowRaw.dayOfWeek ?? rowRaw.day_of_week,
      timeSlot:
        rowRaw.timeSlot ??
        rowRaw.time_slot ??
        (rowRaw.startTime && rowRaw.endTime ? `${rowRaw.startTime.substring(0,5)} - ${rowRaw.endTime.substring(0,5)}` : null),
      startTime: rowRaw.startTime ?? rowRaw.start_time ?? (rowRaw.timeSlot ? rowRaw.timeSlot.split(" - ")[0] : null),
      endTime: rowRaw.endTime ?? rowRaw.end_time ?? (rowRaw.timeSlot ? rowRaw.timeSlot.split(" - ")[1] : null),
      subjectName: rowRaw.subjectName ?? rowRaw.subject_name,
      subjectCode: rowRaw.subjectCode ?? rowRaw.subject_code,
      teacherName: rowRaw.teacherName ?? rowRaw.teacher_name,
      classroomName: rowRaw.classroomName ?? rowRaw.classroom ?? rowRaw.classroom_name,
      batchName: rowRaw.batchName ?? rowRaw.batch_name,
    };

    const start = row.startTime ? row.startTime.substring(0, 5) : null;
    if (!start) return;
    if (!grid[row.dayOfWeek]) grid[row.dayOfWeek] = {};
    if (!grid[row.dayOfWeek][start]) grid[row.dayOfWeek][start] = [];
    grid[row.dayOfWeek][start].push(row);
  });

  // Lunch detection: duration between start & end in minutes
  const detectLunch = (start, end) => {
    if (!start || !end) return false;
    // ensure format "HH:mm" or "HH:mm:ss"
    const s = start.length === 5 ? `${start}:00` : start;
    const e = end.length === 5 ? `${end}:00` : end;
    const dur = (new Date(`1970-01-01T${e}`) - new Date(`1970-01-01T${s}`)) / (1000 * 60);
    return dur >= 50 && dur <= 80; // tolerant for 55±
  };

  return (
    <div style={{ padding: "20px" }}>
      <h2 style={{ textAlign: "center", marginBottom: 20 }}>Weekly Timetable Grid</h2>

      {error && <p style={{ color: "red" }}>{error}</p>}
      {loading && <p>Loading...</p>}

      {!loading && timeSlots.length > 0 && (
        <div style={{ overflowX: "auto" }}>
          <table style={{ width: "100%", borderCollapse: "collapse", background: "white" }}>
            <thead>
              <tr>
                <th style={{ padding: 10, border: "1px solid #ddd", background: "#2c3e50", color: "white", position: "sticky", left: 0, zIndex: 10 }}>
                  Day
                </th>
                {timeSlots.map((ts) => (
                  <th key={ts.id} style={{ padding: 10, border: "1px solid #ddd", background: "#34495e", color: "white", minWidth: "140px", textAlign: "center" }}>
                    {ts.startTime.substring(0, 5)} - {ts.endTime.substring(0, 5)}
                  </th>
                ))}
              </tr>
            </thead>

            <tbody>
              {days.map((day) => (
                <tr key={day}>
                  <td style={{ padding: 10, border: "1px solid #ddd", background: "#ecf0f1", fontWeight: "bold", position: "sticky", left: 0, zIndex: 5 }}>
                    {day}
                  </td>

                  {timeSlots.map((ts) => {
                    const start = ts.startTime.substring(0, 5);
                    const end = ts.endTime.substring(0, 5);
                    const isLunch = detectLunch(start, end);
                    const entries = grid[day]?.[start] || [];

                    return (
                      <td key={ts.id} style={{ border: "1px solid #ddd", padding: 10, minHeight: "70px", backgroundColor: isLunch ? "#fff3cd" : entries.length > 0 ? "#e8f5e9" : "#fafafa" }}>
                        {isLunch ? (
                          <b style={{ color: "#9b6200" }}>LUNCH BREAK</b>
                        ) : entries.length > 0 ? (
                          entries.map((e, idx) => (
                            <div key={idx} style={{ marginBottom: 6, padding: 6, background: "white", borderRadius: 6, boxShadow: "0 1px 4px rgba(0,0,0,0.1)" }}>
                              <b>{e.subjectName} {e.subjectCode ? `(${e.subjectCode})` : ""}</b>
                              <div style={{ fontSize: 12 }}>{e.teacherName}</div>
                              <div style={{ fontSize: 12 }}>Room: {e.classroomName}</div>
                              {e.batchName && <div style={{ fontSize: 11, color: "#8e44ad", marginTop: 2 }}>Batch: {e.batchName}</div>}
                              <div style={{ marginTop: 4, fontSize: 10, padding: "2px 6px", display: "inline-block", background: e.sessionType === "LAB" ? "#3498db" : "#aaa", color: "white", borderRadius: 10 }}>
                                {e.sessionType}
                              </div>
                            </div>
                          ))
                        ) : (
                          <i style={{ color: "#aaa" }}>Free</i>
                        )}
                      </td>
                    );
                  })}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {!loading && timetable.length === 0 && <p>No classes found for this division.</p>}
    </div>
  );
}

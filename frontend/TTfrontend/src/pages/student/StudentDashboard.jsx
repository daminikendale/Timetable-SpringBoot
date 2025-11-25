import React, { useState, useEffect } from "react";

export default function StudentDashboard() {
  const [years, setYears] = useState([]);
  const [divisions, setDivisions] = useState([]);
  const [year, setYear] = useState("");
  const [division, setDivision] = useState("");

  const [timetableData, setTimetableData] = useState([]);
  const [timeSlots, setTimeSlots] = useState([]);
  const [showGrid, setShowGrid] = useState(false);
  const [error, setError] = useState("");

  const days = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"];

  // Load years
  useEffect(() => {
    fetch("http://localhost:8080/api/years/dto")
      .then((r) => r.json())
      .then(setYears)
      .catch(() => setError("Failed to load years"));
  }, []);

  // Load divisions
  useEffect(() => {
    if (!year) {
      setDivisions([]);
      return;
    }

    fetch(`http://localhost:8080/api/divisions?yearId=${year}`)
      .then((r) => r.json())
      .then(setDivisions)
      .catch(() => setError("Failed to load divisions"));
  }, [year]);

  // Load timeslots
  useEffect(() => {
    fetch("http://localhost:8080/api/timeslots")
      .then((r) => r.json())
      .then((data) => {
        const sorted = data.sort((a, b) =>
          a.startTime.localeCompare(b.startTime)
        );

        const formatted = sorted.map((ts) => ({
          id: ts.id,
          start: ts.startTime.substring(0, 5),
          end: ts.endTime.substring(0, 5),
        }));

        setTimeSlots(formatted);
      })
      .catch(() => setError("Failed to load timeslots"));
  }, []);

  // Load timetable
  const loadTimetable = () => {
    if (!division) return;

    fetch(
      `http://localhost:8080/api/scheduled-classes/division/${division}/timetable`
    )
      .then((r) => r.json())
      .then((d) => {
        setTimetableData(d);
        setShowGrid(true);
      })
      .catch(() => setError("Failed to load timetable"));
  };

  // Overlap detection (IMPORTANT FIX)
  const overlaps = (classStart, classEnd, slotStart, slotEnd) => {
    return classStart < slotEnd && classEnd > slotStart;
  };

  const getClassForSlot = (day, slot) => {
    return timetableData.filter(
      (c) =>
        c.dayOfWeek === day &&
        overlaps(
          c.startTime,
          c.endTime,
          slot.start + ":00",
          slot.end + ":00"
        )
    );
  };

  return (
    <div style={{ padding: "20px", maxWidth: "1100px", margin: "auto" }}>
      <h1 style={{ textAlign: "center" }}>Student Timetable</h1>

      {/* Year */}
      <div>
        <label>Select Year:</label>
        <select value={year} onChange={(e) => setYear(e.target.value)}>
          <option value="">--Select--</option>
          {years.map((y) => (
            <option key={y.id} value={y.id}>
              {y.yearName}
            </option>
          ))}
        </select>
      </div>

      {/* Division */}
      <div>
        <label>Select Division:</label>
        <select
          value={division}
          onChange={(e) => setDivision(e.target.value)}
          disabled={!year}
        >
          <option value="">--Select--</option>
          {divisions.map((d) => (
            <option key={d.id} value={d.id}>
              {d.divisionName}
            </option>
          ))}
        </select>
      </div>

      <button onClick={loadTimetable}>View Timetable</button>

      {/* GRID */}
      {showGrid && (
        <table style={{ marginTop: 30, width: "100%", borderCollapse: "collapse" }}>
          <thead>
            <tr>
              <th style={headStyle}>Day</th>
              {timeSlots.map((ts) => (
                <th key={ts.id} style={headStyle}>
                  {ts.start}-{ts.end}
                </th>
              ))}
            </tr>
          </thead>

          <tbody>
            {days.map((day) => (
              <tr key={day}>
                <td style={dayStyle}>{day}</td>

                {timeSlots.map((slot) => {
                  const entries = getClassForSlot(day, slot);

                  return (
                    <td key={slot.id} style={cellStyle}>
                      {entries.length > 0 ? (
                        entries.map((e) => (
                          <div key={e.id} style={{ marginBottom: 6 }}>
                            <b>{e.subjectName}</b>
                            <br />
                            {e.teacherName}
                            <br />
                            <small>{e.classroomName}</small>
                          </div>
                        ))
                      ) : (
                        <span style={{ color: "#bbb" }}>Free</span>
                      )}
                    </td>
                  );
                })}
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

const headStyle = {
  background: "#2c3e50",
  color: "white",
  padding: 8,
};

const dayStyle = {
  background: "#eee",
  padding: 8,
  fontWeight: "bold",
};

const cellStyle = {
  border: "1px solid #ddd",
  padding: 10,
  textAlign: "center",
};

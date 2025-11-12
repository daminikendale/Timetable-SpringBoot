import React, { useEffect, useState } from "react";
import axios from "axios";
import "./TimetableGrid.css"; // optional styling

export default function TimetableGrid({ divisionId }) {
  const [timetable, setTimetable] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  // Define the week structure
  const days = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"];
  const timeSlots = [
    "08:00:00",
    "09:00:00",
    "10:00:00",
    "10:15:00",
    "11:00:00",
    "11:15:00",
    "12:15:00",
    "13:15:00",
  ];

  useEffect(() => {
    if (!divisionId) return;
    setLoading(true);
    axios
      .get(
        `http://localhost:8080/api/scheduled-classes/division/${divisionId}/timetable`
      )
      .then((res) => {
        setTimetable(res.data);
        setError("");
      })
      .catch((err) => {
        console.error(err);
        setError("⚠️ Failed to load timetable");
      })
      .finally(() => setLoading(false));
  }, [divisionId]);

  // ✅ Build a day-time grid that can hold multiple classes per slot
  const grid = {};
  timetable.forEach((entry) => {
    const day = entry.day_of_week;
    const time = entry.start_time;

    if (!grid[day]) grid[day] = {};
    if (!grid[day][time]) grid[day][time] = [];

    grid[day][time].push(entry);
  });

  return (
    <div className="timetable-wrapper">
      <h2 className="text-center mb-3">Weekly Timetable Grid</h2>

      {loading ? (
        <p>Loading timetable...</p>
      ) : error ? (
        <p style={{ color: "red" }}>{error}</p>
      ) : timetable.length === 0 ? (
        <p>No classes found for this division.</p>
      ) : (
        <div className="table-container">
          <table className="timetable-table">
            <thead>
              <tr>
                <th>Day</th>
                {timeSlots.map((slot) => (
                  <th key={slot}>{formatTimeSlot(slot)}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {days.map((day) => (
                <tr key={day}>
                  <td className="day-cell">{day}</td>

                  {timeSlots.map((slot) => (
                    <td key={slot} className="time-cell">
                      {slot === "12:15:00" ? (
                        <div className="lunch-cell">LUNCH BREAK</div>
                      ) : grid[day] && grid[day][slot] ? (
                        grid[day][slot].map((entry, idx) => (
                          <div key={idx} className="class-card">
                            <strong>{entry.subject_name}</strong>
                            <div>{entry.teacher_name}</div>
                            <div>{entry.classroom}</div>
                            <div className="session-type">
                              {entry.session_type}
                            </div>
                          </div>
                        ))
                      ) : (
                        <div className="free-cell">Free</div>
                      )}
                    </td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

// Helper to format 24-hour time to readable label
function formatTimeSlot(time) {
  const timeMap = {
    "08:00:00": "08:00-09:00",
    "09:00:00": "09:00-10:00",
    "10:00:00": "10:00-11:00",
    "10:15:00": "10:15-11:15",
    "11:00:00": "11:00-12:00",
    "11:15:00": "11:15-12:15",
    "12:15:00": "12:15-13:15",
    "13:15:00": "13:15-14:15",
  };
  return timeMap[time] || time;
}

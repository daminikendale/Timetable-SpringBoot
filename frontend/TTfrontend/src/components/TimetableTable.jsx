// TimetableTable.js
import React from "react";

/**
 * TimetableTable
 * props:
 *  - rows: array of scheduled class DTOs
 *  - type: optional string 'teacher' to include division column
 */
function TimetableTable({ rows = [], type }) {
  if (!Array.isArray(rows) || rows.length === 0) {
    return <div>No timetable available.</div>;
  }

  // safe extraction: handle either timeSlot string or startTime/endTime fields
  const splitTimes = (row) => {
    if (row.timeSlot) {
      const parts = row.timeSlot.split(" - ");
      return [parts[0], parts[1]];
    }
    const start = row.startTime ?? row.start_time ?? "";
    const end = row.endTime ?? row.end_time ?? "";
    return [start.substring(0, 5), end.substring(0, 5)];
  };

  return (
    <table border="1" cellPadding={6} style={{ marginTop: 20, width: "100%", borderCollapse: "collapse" }}>
      <thead>
        <tr style={{ background: "#2c3e50", color: "white" }}>
          <th style={{ padding: 8 }}>Day</th>
          <th style={{ padding: 8 }}>Start</th>
          <th style={{ padding: 8 }}>End</th>
          <th style={{ padding: 8 }}>Subject</th>
          <th style={{ padding: 8 }}>Teacher</th>
          {type === "teacher" && <th style={{ padding: 8 }}>Division</th>}
          <th style={{ padding: 8 }}>Room</th>
        </tr>
      </thead>
      <tbody>
        {rows.map((row) => {
          const [startTime, endTime] = splitTimes(row);
          return (
            <tr key={row.id}>
              <td style={{ padding: 6 }}>{row.dayOfWeek ?? row.day_of_week}</td>
              <td style={{ padding: 6 }}>{startTime}</td>
              <td style={{ padding: 6 }}>{endTime}</td>
              <td style={{ padding: 6 }}>{row.subjectName ?? row.subject_name}</td>
              <td style={{ padding: 6 }}>{row.teacherName ?? row.teacher_name}</td>
              {type === "teacher" && <td style={{ padding: 6 }}>{row.divisionName ?? row.division_name}</td>}
              <td style={{ padding: 6 }}>{row.classroomName ?? row.classroom ?? row.classroom_name}</td>
            </tr>
          );
        })}
      </tbody>
    </table>
  );
}

export default TimetableTable;

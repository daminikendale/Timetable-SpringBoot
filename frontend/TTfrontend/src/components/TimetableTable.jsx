import React from 'react';

function TimetableTable({ rows, type }) {
  if (!Array.isArray(rows) || rows.length === 0) {
    return <div>No timetable available.</div>;
  }

  return (
    <table border="1" cellPadding={6} style={{ marginTop: 20, width: "100%" }}>
      <thead>
        <tr>
          <th>Day</th>
          <th>Start</th>
          <th>End</th>
          <th>Subject</th>
          <th>Teacher</th>
          {type === 'teacher' && <th>Division</th>}
          <th>Room</th>
        </tr>
      </thead>
      <tbody>
        {rows.map(row => {
          // Split "08:00 - 09:00" into ["08:00", "09:00"]
          const [startTime, endTime] = row.timeSlot.split(' - ');
          
          return (
            <tr key={row.id}>
              <td>{row.dayOfWeek}</td>
              <td>{startTime}</td>
              <td>{endTime}</td>
              <td>{row.subjectName}</td>
              <td>{row.teacherName}</td>
              {type === 'teacher' && <td>{row.divisionName}</td>}
              <td>{row.classroomNumber}</td>
            </tr>
          );
        })}
      </tbody>
    </table>
  );
}

export default TimetableTable;

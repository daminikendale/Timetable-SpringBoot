import React, { useEffect, useState } from 'react';
import TimetableTable from '../../components/TimetableTable';

function TeacherDashboard() {
  const [rows, setRows] = useState([]);
  const teacherId = 1; // TODO: replace with login/user logic

  useEffect(() => {
    fetch(`http://localhost:8080/api/timetable/by-teacher/${teacherId}`)
      .then(res => res.json())
      .then(data => setRows(data));
  }, [teacherId]);

  return (
    <div style={{ padding: 24 }}>
      <h2>Teacher Timetable</h2>
      <TimetableTable rows={rows} type="teacher" />
    </div>
  );
}
export default TeacherDashboard;

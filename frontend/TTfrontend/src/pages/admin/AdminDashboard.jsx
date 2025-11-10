import React, { useState } from 'react';
import TimetableTable from '../../components/TimetableTable';

function AdminDashboard() {
  const [rows, setRows] = useState([]);
  const [divisionId, setDivisionId] = useState(1); // default division
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

  const createTimetable = async () => {
    setLoading(true);
    setMessage('Generating...');
    try {
      const res = await fetch('http://localhost:8080/api/timetable-generator/generate/ODD', { method: 'POST' });
      const text = await res.text();
      setMessage(text);
    } catch {
      setMessage('Failed to generate timetable');
    }
    setLoading(false);
  };

  const viewTimetable = async () => {
    setLoading(true);
    setMessage('Loading...');
    try {
      const res = await fetch(`http://localhost:8080/api/timetable/by-division/${divisionId}`);
      const data = await res.json();
      setRows(Array.isArray(data) ? data : []);
      setMessage('');
    } catch {
      setRows([]);
      setMessage('Failed to fetch timetable');
    }
    setLoading(false);
  };

  return (
    <div style={{ padding: 32 }}>
      <h2>Admin Dashboard</h2>
      <div style={{ display: 'flex', gap: 24, marginBottom: 24 }}>
        <button onClick={viewTimetable} disabled={loading}>View Timetable</button>
        <button onClick={createTimetable} disabled={loading}>Create Timetable</button>
      </div>
      {message && <div>{message}</div>}
      <TimetableTable rows={rows} type="admin" />
    </div>
  );
}
export default AdminDashboard;

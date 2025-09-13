import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import Sidebar from './components/Sidebar.jsx';
import Teachers from './pages/Teachers.jsx';
import Subjects from './pages/Subjects.jsx';
import Classrooms from './pages/Classrooms.jsx';
import Departments from './pages/Departments.jsx';
import Divisions from './pages/Divisions.jsx';
import Semesters from './pages/Semesters.jsx';
import TimeSlots from './pages/TimeSlots.jsx';
import Breaks from './pages/Breaks.jsx';
import Tutorials from './pages/Tutorials.jsx';
import WorkingHours from './pages/WorkingHours.jsx';
import Electives from './pages/Electives.jsx';
import TeacherAllocations from './pages/TeacherAllocations.jsx';
import TimetableChanges from './pages/TimetableChanges';


export default function App() {
  return (
    <div style={{ display: 'flex', minHeight: '100vh' }}>
      <Sidebar />
      <main style={{ flex: 1, padding: '24px' }}>
        <Routes>
          <Route path="/timetable-changes" element={<TimetableChanges />} />
          <Route path="/" element={<Navigate to="/teachers" replace />} />
          <Route path="/teachers" element={<Teachers />} />
          <Route path="/subjects" element={<Subjects />} />
          <Route path="/classrooms" element={<Classrooms />} />
          <Route path="/departments" element={<Departments />} />
          <Route path="/divisions" element={<Divisions />} />
          <Route path="/semesters" element={<Semesters />} />
          <Route path="/timeslots" element={<TimeSlots />} />
          <Route path="/breaks" element={<Breaks />} />
          <Route path="/tutorials" element={<Tutorials />} />
          <Route path="/working-hours" element={<WorkingHours />} />
          <Route path="/electives" element={<Electives />} />
          <Route path="/teacher-allocations" element={<TeacherAllocations />} />
        </Routes>
      </main>
    </div>
  );
}

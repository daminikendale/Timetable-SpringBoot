import React from 'react';
import { useNavigate } from 'react-router-dom';

function RoleSelect() {
  const navigate = useNavigate();

  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      background: 'linear-gradient(135deg, #c9e7ff, #fff7e6)',
    }}>
      <h1 style={{ fontSize: '2.5rem', fontWeight: 'bold', marginBottom: 10 }}>Welcome to Timetable</h1>
      <p style={{ marginBottom: 32, fontSize: '1.2rem' }}>Select your role:</p>
      <div style={{ display: 'flex', gap: 32 }}>
        <button
          style={btnStyle}
          onClick={() => navigate('/student')}
        >Student</button>
        <button
          style={btnStyle}
          onClick={() => navigate('/teacher')}
        >Teacher</button>
        <button
          style={btnStyle}
          onClick={() => navigate('/admin')}
        >Admin</button>
      </div>
    </div>
  );
}

const btnStyle = {
  padding: '20px 40px',
  fontSize: '1.2rem',
  borderRadius: '12px',
  border: 'none',
  background: 'linear-gradient(90deg,#5b9df7 10%, #4bd7c7 90%)',
  color: '#fff',
  fontWeight: 600,
  letterSpacing: '.05em',
  boxShadow: '0 2px 12px rgba(60,100,180,0.08)',
  cursor: 'pointer',
  transition: 'transform .1s',
};

export default RoleSelect;

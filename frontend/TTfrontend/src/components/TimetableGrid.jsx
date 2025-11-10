import React, { useState, useEffect } from 'react';

const TimetableGrid = ({ divisionId }) => {
  const [timetableData, setTimetableData] = useState([]);
  const [loading, setLoading] = useState(true);

  // Define all unique time slots
  const timeSlots = [
    { start: '08:00', end: '09:00' },
    { start: '09:00', end: '10:00' },
    { start: '10:00', end: '10:15' },
    { start: '10:15', end: '11:15' },
    { start: '11:00', end: '11:15' },
    { start: '11:15', end: '12:15' },
    { start: '12:15', end: '13:15' }, // LUNCH BREAK
    { start: '13:15', end: '14:15' },
    { start: '14:15', end: '15:15' },
    { start: '15:15', end: '16:15' },
    { start: '16:15', end: '17:15' }
  ];

  const days = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY'];

  useEffect(() => {
    fetchTimetable();
  }, [divisionId]);

  const fetchTimetable = async () => {
    try {
      // Update this URL to match your backend endpoint
      const response = await fetch(
        `http://localhost:8080/api/scheduled-classes/division/${divisionId}/timetable`
      );
      const data = await response.json();
      setTimetableData(data);
      setLoading(false);
    } catch (error) {
      console.error('Error fetching timetable:', error);
      setLoading(false);
    }
  };

  // Get class for a specific day and time slot
  const getClassForSlot = (day, timeSlot) => {
    return timetableData.find(
      (item) =>
        item.day_of_week === day &&
        item.start_time === timeSlot.start + ':00.000000' &&
        item.end_time === timeSlot.end + ':00.000000'
    );
  };

  // Check if time slot is lunch break
  const isLunchBreak = (timeSlot) => {
    return timeSlot.start === '12:15' && timeSlot.end === '13:15';
  };

  if (loading) {
    return <div style={styles.loading}>Loading timetable...</div>;
  }

  return (
    <div style={styles.container}>
      <h2 style={styles.title}>Timetable - SY-A</h2>
      <div style={styles.tableWrapper}>
        <table style={styles.table}>
          <thead>
            <tr>
              <th style={styles.dayHeader}>Day</th>
              {timeSlots.map((slot, index) => (
                <th key={index} style={styles.timeHeader}>
                  {slot.start}-{slot.end}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {days.map((day) => (
              <tr key={day}>
                <td style={styles.dayCell}>{day}</td>
                {timeSlots.map((slot, index) => {
                  const classInfo = getClassForSlot(day, slot);
                  const isBreak = isLunchBreak(slot);

                  return (
                    <td
                      key={`${day}-${index}`}
                      style={{
                        ...styles.classCell,
                        ...(isBreak ? styles.lunchBreak : {}),
                        ...(classInfo ? styles.hasClass : styles.emptySlot),
                      }}
                    >
                      {isBreak ? (
                        <div style={styles.breakLabel}>LUNCH BREAK</div>
                      ) : classInfo ? (
                        <div style={styles.classInfo}>
                          <div style={styles.subjectName}>
                            {classInfo.subject_name}
                          </div>
                          <div style={styles.teacherName}>
                            {classInfo.teacher_name}
                          </div>
                          <div style={styles.classroomName}>
                            {classInfo.classroom}
                          </div>
                          {classInfo.session_type === 'LAB' && (
                            <div style={styles.sessionType}>LAB</div>
                          )}
                        </div>
                      ) : (
                        <div style={styles.emptyLabel}>Free</div>
                      )}
                    </td>
                  );
                })}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

// Inline styles
const styles = {
  container: {
    padding: '20px',
    maxWidth: '100%',
    overflowX: 'auto',
  },
  title: {
    textAlign: 'center',
    marginBottom: '20px',
    color: '#333',
  },
  tableWrapper: {
    overflowX: 'auto',
  },
  table: {
    width: '100%',
    borderCollapse: 'collapse',
    boxShadow: '0 2px 8px rgba(0, 0, 0, 0.1)',
    background: 'white',
  },
  dayHeader: {
    backgroundColor: '#2c3e50',
    color: 'white',
    fontWeight: 'bold',
    minWidth: '100px',
    border: '1px solid #ddd',
    padding: '12px',
    textAlign: 'center',
  },
  timeHeader: {
    backgroundColor: '#34495e',
    color: 'white',
    fontWeight: 'bold',
    fontSize: '12px',
    minWidth: '120px',
    border: '1px solid #ddd',
    padding: '12px',
    textAlign: 'center',
  },
  dayCell: {
    backgroundColor: '#ecf0f1',
    fontWeight: 'bold',
    color: '#2c3e50',
    border: '1px solid #ddd',
    padding: '12px',
    textAlign: 'center',
  },
  classCell: {
    border: '1px solid #ddd',
    padding: '12px',
    textAlign: 'center',
    verticalAlign: 'middle',
    minHeight: '80px',
  },
  hasClass: {
    backgroundColor: '#e8f5e9',
  },
  emptySlot: {
    backgroundColor: '#fafafa',
    color: '#999',
  },
  lunchBreak: {
    backgroundColor: '#fff3cd',
  },
  breakLabel: {
    fontWeight: 'bold',
    color: '#856404',
  },
  classInfo: {
    display: 'flex',
    flexDirection: 'column',
    gap: '4px',
  },
  subjectName: {
    fontWeight: 'bold',
    color: '#2c3e50',
    fontSize: '13px',
  },
  teacherName: {
    color: '#7f8c8d',
    fontSize: '12px',
  },
  classroomName: {
    color: '#95a5a6',
    fontSize: '11px',
  },
  sessionType: {
    display: 'inline-block',
    backgroundColor: '#3498db',
    color: 'white',
    padding: '2px 8px',
    borderRadius: '12px',
    fontSize: '10px',
    marginTop: '4px',
  },
  emptyLabel: {
    color: '#bbb',
    fontStyle: 'italic',
  },
  loading: {
    textAlign: 'center',
    padding: '40px',
    fontSize: '18px',
    color: '#666',
  },
};

export default TimetableGrid;

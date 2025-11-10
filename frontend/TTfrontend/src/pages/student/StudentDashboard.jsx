import React, { useState, useEffect } from 'react';

export default function StudentDashboard() {
  const [years, setYears] = useState([]);
  const [divisions, setDivisions] = useState([]);
  const [year, setYear] = useState('');
  const [division, setDivision] = useState('');
  const [timetableData, setTimetableData] = useState([]);
  const [error, setError] = useState('');
  const [showGrid, setShowGrid] = useState(false);

  // ✅ FIXED: These are the EXACT time slots from your database screenshot
  const timeSlots = [
    { start: '08:00', end: '09:00' },
    { start: '09:00', end: '10:00' },
    { start: '10:00', end: '11:00' },
    { start: '10:15', end: '11:15' },
    { start: '11:00', end: '12:00' },
    { start: '11:15', end: '12:15' },
    { start: '12:15', end: '13:15' }, // LUNCH BREAK
    { start: '13:15', end: '14:15' },
    { start: '14:15', end: '15:15' },
    { start: '15:15', end: '16:15' },
    { start: '16:15', end: '17:15' }
  ];

  const days = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY'];

  // Load years on mount
  useEffect(() => {
    fetch('http://localhost:8080/api/years/dto')
      .then(r => {
        if (!r.ok) throw new Error('Failed to load years');
        return r.json();
      })
      .then(d => {
        console.log('✅ Years loaded:', d);
        setYears(Array.isArray(d) ? d : []);
      })
      .catch(e => {
        console.error('❌ Error loading years:', e);
        setError('Failed to load years');
      });
  }, []);

  // Load divisions when year changes
  useEffect(() => {
    if (!year) {
      setDivisions([]);
      setDivision('');
      return;
    }
    
    console.log('🔍 Fetching divisions for year:', year);
    setError('');
    
    fetch(`http://localhost:8080/api/divisions?yearId=${year}`)
      .then(r => {
        console.log('📡 Division response status:', r.status);
        if (!r.ok) {
          throw new Error(`Server error: ${r.status}`);
        }
        return r.json();
      })
      .then(d => {
        console.log('✅ Divisions loaded:', d);
        setDivisions(Array.isArray(d) ? d : []);
        setDivision('');
      })
      .catch(e => {
        console.error('❌ Error loading divisions:', e);
        setError(`Failed to load divisions: ${e.message}`);
        setDivisions([]);
      });
  }, [year]);

  // Load timetable
  const loadTimetable = () => {
    if (!year || !division) {
      alert('Please select both year and division');
      return;
    }
    
    setError('');
    setShowGrid(false);
    
    fetch(`http://localhost:8080/api/scheduled-classes/division/${division}/timetable`)
      .then(r => {
        if (!r.ok) throw new Error('Failed to load timetable');
        return r.json();
      })
      .then(d => {
        console.log('✅ Timetable loaded:', d);
        setTimetableData(Array.isArray(d) ? d : []);
        setShowGrid(true);
      })
      .catch(e => {
        console.error('❌ Error loading timetable:', e);
        setError(`Failed to load timetable: ${e.message}`);
      });
  };

  // Get class for a specific day and time slot
  const getClassForSlot = (day, timeSlot) => {
    return timetableData.find(
      (item) =>
        item.day_of_week === day &&
        item.start_time === timeSlot.start + ':00' &&
        item.end_time === timeSlot.end + ':00'
    );
  };

  // Check if time slot is lunch break
  const isLunchBreak = (timeSlot) => {
    return timeSlot.start === '12:15' && timeSlot.end === '13:15';
  };

  return (
    <div style={{padding:'20px',maxWidth:'100%',margin:'auto',fontFamily:'Arial, sans-serif'}}>
      <h1 style={{color:'#333',marginBottom:'30px',textAlign:'center'}}>Student Timetable</h1>
      
      {/* ERROR MESSAGE */}
      {error && (
        <div style={{
          padding:'15px',
          background:'#ffebee',
          color:'#c62828',
          borderRadius:'6px',
          marginBottom:'20px',
          border:'1px solid #ef5350',
          maxWidth:'1100px',
          margin:'0 auto 20px auto'
        }}>
          ⚠️ {error}
        </div>
      )}
      
      {/* SELECTION CONTAINER */}
      <div style={{maxWidth:'1100px',margin:'0 auto 40px auto'}}>
        {/* YEAR DROPDOWN */}
        <div style={{marginBottom:'20px'}}>
          <label style={{display:'block',marginBottom:'8px',fontWeight:'bold',fontSize:'14px',color:'#555'}}>
            Select Year:
          </label>
          <select 
            value={year} 
            onChange={e => setYear(e.target.value)}
            style={{
              width:'100%',
              padding:'12px',
              fontSize:'16px',
              borderRadius:'6px',
              border:'2px solid #ddd',
              backgroundColor:'white',
              cursor:'pointer',
              outline:'none'
            }}
          >
            <option value="">-- Select Year --</option>
            {years.map(y => <option key={y.id} value={y.id}>{y.yearName}</option>)}
          </select>
        </div>

        {/* DIVISION DROPDOWN */}
        <div style={{marginBottom:'20px'}}>
          <label style={{display:'block',marginBottom:'8px',fontWeight:'bold',fontSize:'14px',color:'#555'}}>
            Select Division:
          </label>
          <select 
            value={division}
            onChange={e => setDivision(e.target.value)}
            disabled={!year}
            style={{
              width:'100%',
              padding:'12px',
              fontSize:'16px',
              borderRadius:'6px',
              border:'2px solid #ddd',
              backgroundColor:!year?'#f5f5f5':'white',
              cursor:!year?'not-allowed':'pointer',
              outline:'none'
            }}
          >
            <option value="">-- Select Division --</option>
            {divisions.map(d => <option key={d.id} value={d.id}>{d.divisionName}</option>)}
          </select>
          {year && divisions.length === 0 && !error && (
            <small style={{color:'#999',display:'block',marginTop:'6px',fontStyle:'italic'}}>
              No divisions available for this year
            </small>
          )}
        </div>

        {/* VIEW BUTTON */}
        <button 
          onClick={loadTimetable}
          disabled={!year || !division}
          style={{
            padding:'14px 28px',
            fontSize:'16px',
            fontWeight:'bold',
            backgroundColor:(!year||!division)?'#ccc':'#007bff',
            color:'white',
            border:'none',
            borderRadius:'6px',
            cursor:(!year||!division)?'not-allowed':'pointer',
            transition:'background-color 0.2s',
            width:'100%'
          }}
          onMouseOver={e => {if(year && division) e.target.style.backgroundColor='#0056b3'}}
          onMouseOut={e => {if(year && division) e.target.style.backgroundColor='#007bff'}}
        >
          View Timetable
        </button>
      </div>

      {/* TIMETABLE GRID */}
      {showGrid && timetableData.length > 0 && (
        <div style={{marginTop:'40px',overflowX:'auto'}}>
          <h2 style={{textAlign:'center',marginBottom:'20px',color:'#333'}}>
            Weekly Timetable Grid
          </h2>
          <div style={{overflowX:'auto'}}>
            <table style={{
              width:'100%',
              minWidth:'1000px',
              borderCollapse:'collapse',
              boxShadow:'0 2px 8px rgba(0, 0, 0, 0.1)',
              background:'white',
              margin:'0 auto'
            }}>
              <thead>
                <tr>
                  <th style={{
                    backgroundColor:'#2c3e50',
                    color:'white',
                    fontWeight:'bold',
                    minWidth:'100px',
                    border:'1px solid #ddd',
                    padding:'12px',
                    textAlign:'center',
                    position:'sticky',
                    left:0,
                    zIndex:10
                  }}>Day</th>
                  {timeSlots.map((slot, index) => (
                    <th key={index} style={{
                      backgroundColor:'#34495e',
                      color:'white',
                      fontWeight:'bold',
                      fontSize:'12px',
                      minWidth:'140px',
                      border:'1px solid #ddd',
                      padding:'12px',
                      textAlign:'center'
                    }}>
                      {slot.start}-{slot.end}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {days.map((day) => (
                  <tr key={day}>
                    <td style={{
                      backgroundColor:'#ecf0f1',
                      fontWeight:'bold',
                      color:'#2c3e50',
                      border:'1px solid #ddd',
                      padding:'12px',
                      textAlign:'center',
                      position:'sticky',
                      left:0,
                      zIndex:5
                    }}>{day}</td>
                    {timeSlots.map((slot, index) => {
                      const classInfo = getClassForSlot(day, slot);
                      const isBreak = isLunchBreak(slot);

                      return (
                        <td
                          key={`${day}-${index}`}
                          style={{
                            border:'1px solid #ddd',
                            padding:'12px',
                            textAlign:'center',
                            verticalAlign:'middle',
                            minHeight:'80px',
                            backgroundColor: isBreak ? '#fff3cd' : classInfo ? '#e8f5e9' : '#fafafa',
                            color: !classInfo && !isBreak ? '#999' : 'inherit'
                          }}
                        >
                          {isBreak ? (
                            <div style={{fontWeight:'bold',color:'#856404'}}>
                              LUNCH BREAK
                            </div>
                          ) : classInfo ? (
                            <div style={{display:'flex',flexDirection:'column',gap:'4px'}}>
                              <div style={{fontWeight:'bold',color:'#2c3e50',fontSize:'13px'}}>
                                {classInfo.subject_name}
                              </div>
                              <div style={{color:'#7f8c8d',fontSize:'12px'}}>
                                {classInfo.teacher_name}
                              </div>
                              <div style={{color:'#95a5a6',fontSize:'11px'}}>
                                {classInfo.classroom}
                              </div>
                              {classInfo.session_type === 'LAB' && (
                                <div style={{
                                  display:'inline-block',
                                  backgroundColor:'#3498db',
                                  color:'white',
                                  padding:'2px 8px',
                                  borderRadius:'12px',
                                  fontSize:'10px',
                                  marginTop:'4px'
                                }}>LAB</div>
                              )}
                            </div>
                          ) : (
                            <div style={{color:'#bbb',fontStyle:'italic'}}>Free</div>
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
      )}

      {showGrid && timetableData.length === 0 && (
        <div style={{
          textAlign:'center',
          padding:'40px',
          fontSize:'16px',
          color:'#666',
          backgroundColor:'#f9f9f9',
          borderRadius:'8px',
          marginTop:'40px'
        }}>
          No classes scheduled for this division yet.
        </div>
      )}
    </div>
  );
}

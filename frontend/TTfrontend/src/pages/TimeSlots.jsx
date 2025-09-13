import React from 'react';
import http from '../api/http';
import BaseCrud from '../components/BaseCrud.jsx';

export default function TimeSlots() {
  return (
    <BaseCrud
      title="Time Slots"
      columns={[
        { field: 'id', headerName: 'ID', width: 80 },
        { field: 'startTime', headerName: 'Start', width: 150 },
        { field: 'endTime', headerName: 'End', width: 150 }
      ]}
      formFields={[
        { name: 'startTime', label: 'Start (HH:mm)', required: true },
        { name: 'endTime', label: 'End (HH:mm)', required: true }
      ]}
      fetchList={() => http.get('/timeslots').then(r => r.data)}
      createItem={(payload) => http.post('/timeslots', payload)}
      updateItem={(id, payload) => http.post('/timeslots', { id, ...payload })}
      deleteItem={(id) => http.delete(`/timeslots/${id}`)}
    />
  );
}

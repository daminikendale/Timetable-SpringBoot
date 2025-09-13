import React from 'react';
import http from '../api/http';
import BaseCrud from '../components/BaseCrud.jsx';

export default function WorkingHours() {
  return (
    <BaseCrud
      title="Working Hours"
      columns={[
        { field: 'id', headerName: 'ID', width: 80 },
        { field: 'startTime', headerName: 'Start', width: 150 },
        { field: 'endTime', headerName: 'End', width: 150 }
      ]}
      formFields={[
        { name: 'startTime', label: 'Start (HH:mm)', required: true },
        { name: 'endTime', label: 'End (HH:mm)', required: true }
      ]}
      fetchList={() => http.get('/working-hours').then(r => r.data)}
      createItem={(payload) => http.post('/working-hours', payload)}
      updateItem={(id, payload) => http.put(`/working-hours/${id}`, payload)}
      deleteItem={(id) => http.delete(`/working-hours/${id}`)}
    />
  );
}

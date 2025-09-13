import React from 'react';
import http from '../api/http';
import BaseCrud from '../components/BaseCrud.jsx';

export default function Breaks() {
  return (
    <BaseCrud
      title="Breaks"
      columns={[
        { field: 'id', headerName: 'ID', width: 80 },
        { field: 'name', headerName: 'Name', flex: 1 },
        { field: 'startTime', headerName: 'Start', width: 150 },
        { field: 'endTime', headerName: 'End', width: 150 }
      ]}
      formFields={[
        { name: 'name', label: 'Name', required: true },
        { name: 'startTime', label: 'Start (HH:mm)', required: true },
        { name: 'endTime', label: 'End (HH:mm)', required: true }
      ]}
      fetchList={() => http.get('/breaks').then(r => r.data)}
      createItem={(payload) => http.post('/breaks/bulk', [payload])}
      // No update/delete endpoints exposed
    />
  );
}

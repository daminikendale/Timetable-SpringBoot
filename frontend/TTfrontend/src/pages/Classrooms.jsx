import React from 'react';
import http from '../api/http';
import BaseCrud from '../components/BaseCrud.jsx';

export default function Classrooms() {
  return (
    <BaseCrud
      title="Classrooms"
      columns={[
        { field: 'id', headerName: 'ID', width: 80 },
        { field: 'name', headerName: 'Name', flex: 1 },
        { field: 'capacity', headerName: 'Capacity', width: 120 },
        { field: 'type', headerName: 'Type (NORMAL/LAB)', width: 180 }
      ]}
      formFields={[
        { name: 'name', label: 'Name', required: true },
        { name: 'capacity', label: 'Capacity', type: 'number', required: true },
        { name: 'type', label: 'Type (NORMAL/LAB)', required: true }
      ]}
      fetchList={() => http.get('/classrooms').then(r => r.data)}
      createItem={(payload) => http.post('/classrooms', payload)}
      updateItem={(id, payload) => http.post('/classrooms', { id, ...payload })}
      deleteItem={(id) => http.delete(`/classrooms/${id}`)}
      toPayload={(f) => ({
        name: f.name,
        capacity: Number(f.capacity),
        type: f.type
      })}
    />
  );
}

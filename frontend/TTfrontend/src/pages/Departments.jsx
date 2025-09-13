import React from 'react';
import http from '../api/http';
import BaseCrud from '../components/BaseCrud.jsx';

export default function Departments() {
  return (
    <BaseCrud
      title="Departments"
      columns={[
        { field: 'id', headerName: 'ID', width: 80 },
        { field: 'name', headerName: 'Name', flex: 1 }
      ]}
      formFields={[
        { name: 'name', label: 'Name', required: true }
      ]}
      fetchList={() => http.get('/departments').then(r => r.data)}
      createItem={(payload) => http.post('/departments', payload)}
      updateItem={(id, payload) => http.post('/departments', { id, ...payload })}
      deleteItem={(id) => http.delete(`/departments/${id}`)}
    />
  );
}

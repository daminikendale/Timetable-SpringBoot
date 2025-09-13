import React from 'react';
import http from '../api/http';
import BaseCrud from '../components/BaseCrud.jsx';

export default function Teachers() {
  return (
    <BaseCrud
      title="Teachers"
      columns={[
        { field: 'id', headerName: 'ID', width: 80 },
        { field: 'name', headerName: 'Name', flex: 1 },
        { field: 'email', headerName: 'Email', flex: 1 },
        { field: 'employeeId', headerName: 'Employee ID', flex: 1 }
      ]}
      formFields={[
        { name: 'name', label: 'Name', required: true },
        { name: 'email', label: 'Email', required: true },
        { name: 'employeeId', label: 'Employee ID', required: true }
      ]}
      fetchList={() => http.get('/teachers').then(r => r.data)}
      createItem={(payload) => http.post('/teachers', payload)}
      updateItem={(id, payload) => http.post('/teachers', { id, ...payload })}
      deleteItem={(id) => http.delete(`/teachers/${id}`)}
    />
  );
}

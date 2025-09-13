import React from 'react';
import http from '../api/http';
import BaseCrud from '../components/BaseCrud.jsx';

export default function Divisions() {
  return (
    <BaseCrud
      title="Divisions"
      columns={[
        { field: 'id', headerName: 'ID', width: 80 },
        { field: 'name', headerName: 'Name', flex: 1 },
        { field: 'yearNumber', headerName: 'Year', width: 100 },
        { field: 'semesterNumber', headerName: 'Semester', width: 120 }
      ]}
      formFields={[
        { name: 'name', label: 'Name', required: true },
        { name: 'year_id', label: 'Year ID', type: 'number', required: true },
        { name: 'semester_id', label: 'Semester ID', type: 'number', required: true }
      ]}
      fetchList={() => http.get('/divisions').then(r => r.data)}
      createItem={(payload) => http.post('/divisions', payload)}
      toPayload={(f) => ({
        name: f.name,
        year: { id: Number(f.year_id) },
        semester: { id: Number(f.semester_id) }
      })}
      // No update/delete endpoints exposed in controller; left disabled
    />
  );
}

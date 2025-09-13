import React from 'react';
import http from '../api/http';
import BaseCrud from '../components/BaseCrud.jsx';

export default function Semesters() {
  return (
    <BaseCrud
      title="Semesters"
      columns={[
        { field: 'id', headerName: 'ID', width: 80 },
        { field: 'semesterNumber', headerName: 'Semester', width: 140 },
        { field: 'yearNumber', headerName: 'Year', width: 100 }
      ]}
      formFields={[
        { name: 'semesterNumber', label: 'Semester Number', type: 'number', required: true },
        { name: 'year_id', label: 'Year ID', type: 'number', required: true }
      ]}
      fetchList={() => http.get('/semesters/dto').then(r => r.data)}
      createItem={(payload) => http.post('/semesters', { semesterNumber: Number(payload.semesterNumber), year: { id: Number(payload.year_id) } })}
      updateItem={(id, payload) => http.post('/semesters', { id, semesterNumber: Number(payload.semesterNumber), year: { id: Number(payload.year_id) } })}
      deleteItem={(id) => http.delete(`/semesters/${id}`)}
      fromRow={(row) => ({ semesterNumber: row.semesterNumber, year_id: row.year_id })}
      toPayload={(f) => ({ semesterNumber: Number(f.semesterNumber), year_id: Number(f.year_id) })}
    />
  );
}

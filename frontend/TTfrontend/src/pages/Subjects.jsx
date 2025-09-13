import React from 'react';
import http from '../api/http';
import BaseCrud from '../components/BaseCrud.jsx';

export default function Subjects() {
  return (
    <BaseCrud
      title="Subjects"
      columns={[
        { field: 'id', headerName: 'ID', width: 80 },
        { field: 'name', headerName: 'Name', flex: 1 },
        { field: 'type', headerName: 'Type', width: 120 },
        { field: 'credits', headerName: 'Credits', width: 120 },
        { field: 'category', headerName: 'Category', width: 180 },
        { field: 'year_id', headerName: 'Year ID', width: 120 },
        { field: 'semester_id', headerName: 'Semester ID', width: 140 }
      ]}
      formFields={[
        { name: 'name', label: 'Name', required: true },
        { name: 'type', label: 'Type (THEORY/LAB)', required: true },
        { name: 'credits', label: 'Credits', type: 'number', required: true },
        { name: 'category', label: 'Category (REGULAR/PROGRAM_ELECTIVE/OPEN_ELECTIVE)', required: true },
        { name: 'year_id', label: 'Year ID', type: 'number', required: true },
        { name: 'semester_id', label: 'Semester ID', type: 'number', required: true }
      ]}
      fetchList={() => http.get('/subjects/dto').then(r => r.data)}
      createItem={(payload) => http.post('/subjects/bulk', [payload])}
      toPayload={(f) => ({
        name: f.name,
        type: f.type,
        credits: Number(f.credits),
        category: f.category,
        year_id: Number(f.year_id),
        semester_id: Number(f.semester_id)
      })}
    />
  );
}

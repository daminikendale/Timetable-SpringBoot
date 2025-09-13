import React from 'react';
import http from '../api/http';
import BaseCrud from '../components/BaseCrud.jsx';

export default function Tutorials() {
  return (
    <BaseCrud
      title="Tutorials"
      columns={[
        { field: 'id', headerName: 'ID', width: 80 },
        { field: 'subject', headerName: 'Subject', valueGetter: v => v.row.subject?.name, flex: 1 },
        { field: 'division', headerName: 'Division', valueGetter: v => v.row.division?.name, width: 160 },
        { field: 'classroom', headerName: 'Classroom', valueGetter: v => v.row.classroom?.name, width: 160 },
        { field: 'durationInHours', headerName: 'Hours', width: 120 },
        { field: 'teacher', headerName: 'Teacher', valueGetter: v => v.row.teacher?.name, width: 200 }
      ]}
      formFields={[
        { name: 'subject_id', label: 'Subject ID', type: 'number', required: true },
        { name: 'division_id', label: 'Division ID (optional)', type: 'number' },
        { name: 'classroom_id', label: 'Classroom ID', type: 'number', required: true },
        { name: 'durationInHours', label: 'Duration Hours', type: 'number', required: true },
        { name: 'teacher_id', label: 'Teacher ID', type: 'number', required: true }
      ]}
      fetchList={() => http.get('/tutorials').then(r => r.data)}
      createItem={(payload) => http.post('/tutorials', payload)}
      updateItem={(id, payload) => http.put(`/tutorials/${id}`, payload)}
      deleteItem={(id) => http.delete(`/tutorials/${id}`)}
      toPayload={(f) => ({
        subject: { id: Number(f.subject_id) },
        division: f.division_id ? { id: Number(f.division_id) } : null,
        classroom: { id: Number(f.classroom_id) },
        durationInHours: Number(f.durationInHours),
        teacher: { id: Number(f.teacher_id) }
      })}
      fromRow={(row) => ({
        subject_id: row.subject?.id,
        division_id: row.division?.id,
        classroom_id: row.classroom?.id,
        durationInHours: row.durationInHours,
        teacher_id: row.teacher?.id
      })}
    />
  );
}

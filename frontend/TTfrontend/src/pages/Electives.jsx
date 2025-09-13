import React, { useEffect, useState } from 'react';
import {
  Box, Stack, TextField, Button, Snackbar, Alert, Divider
} from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import http from '../api/http';

export default function Electives() {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [toast, setToast] = useState({ open: false, msg: '', sev: 'success' });

  const load = async () => {
    setLoading(true);
    try {
      const data = await http.get('/elective-allocations').then(r => r.data);
      setRows(data || []);
    } catch {
      setToast({ open: true, msg: 'Load failed', sev: 'error' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  // Manual create form
  const [form, setForm] = useState({
    subject_id: '', teacher_id: '', year_id: '', semester_id: '',
    credits: '', divisionName: ''
  });

  const createManual = async () => {
    try {
      const payload = {
        subject: { id: Number(form.subject_id) },
        teacher: { id: Number(form.teacher_id) },
        year: { id: Number(form.year_id) },
        semester: { id: Number(form.semester_id) },
        credits: Number(form.credits),
        divisionName: form.divisionName || null
      };
      await http.post('/elective-allocations', payload);
      setToast({ open: true, msg: 'Created', sev: 'success' });
      setForm({ subject_id: '', teacher_id: '', year_id: '', semester_id: '', credits: '', divisionName: '' });
      load();
    } catch {
      setToast({ open: true, msg: 'Create failed', sev: 'error' });
    }
  };

  const remove = async (id) => {
    if (!window.confirm('Delete this allocation?')) return;
    try {
      await http.delete(`/elective-allocations/${id}`);
      setToast({ open: true, msg: 'Deleted', sev: 'success' });
      load();
    } catch {
      setToast({ open: true, msg: 'Delete failed', sev: 'error' });
    }
  };

  // Auto-allocate form
  const [autoForm, setAutoForm] = useState({
    subjectId: '', yearId: '', semesterId: '', teacherId: '',
    totalStudents: '', maxCapacity: ''
  });

  const autoAllocate = async () => {
    try {
      const params = new URLSearchParams({
        subjectId: autoForm.subjectId,
        yearId: autoForm.yearId,
        semesterId: autoForm.semesterId,
        teacherId: autoForm.teacherId,
        totalStudents: autoForm.totalStudents,
        maxCapacity: autoForm.maxCapacity
      });
      await http.post(`/elective-allocations/auto-allocate?${params.toString()}`);
      setToast({ open: true, msg: 'Auto-allocated', sev: 'success' });
      setAutoForm({ subjectId: '', yearId: '', semesterId: '', teacherId: '', totalStudents: '', maxCapacity: '' });
      load();
    } catch {
      setToast({ open: true, msg: 'Auto-allocate failed', sev: 'error' });
    }
  };

  return (
    <Box>
      <h2>Elective Allocations</h2>

      <div style={{ height: 520, width: '100%', background: '#fff' }}>
        <DataGrid
          rows={rows}
          getRowId={(r) => r.id}
          loading={loading}
          columns={[
            { field: 'id', headerName: 'ID', width: 80 },
            { field: 'subject', headerName: 'Subject', valueGetter: v => v.row.subject?.name, flex: 1 },
            { field: 'teacher', headerName: 'Teacher', valueGetter: v => v.row.teacher?.name, width: 180 },
            { field: 'year', headerName: 'Year', valueGetter: v => v.row.year?.yearNumber, width: 100 },
            { field: 'semester', headerName: 'Semester', valueGetter: v => v.row.semester?.semesterNumber, width: 120 },
            { field: 'credits', headerName: 'Credits', width: 120 },
            { field: 'divisionName', headerName: 'Group', width: 120 },
            {
              field: '_actions', headerName: 'Actions', width: 120, sortable: false, filterable: false,
              renderCell: (params) => (
                <Button color="error" onClick={() => remove(params.row.id)}>Delete</Button>
              )
            }
          ]}
          pagination
          pageSizeOptions={[10, 25, 50]}
          disableRowSelectionOnClick
        />
      </div>

      <Divider sx={{ my: 3 }} />

      <h3>Manual Create</h3>
      <Stack direction="row" gap={2} flexWrap="wrap">
        <TextField label="Subject ID" value={form.subject_id} onChange={e => setForm(s => ({ ...s, subject_id: e.target.value }))} />
        <TextField label="Teacher ID" value={form.teacher_id} onChange={e => setForm(s => ({ ...s, teacher_id: e.target.value }))} />
        <TextField label="Year ID" value={form.year_id} onChange={e => setForm(s => ({ ...s, year_id: e.target.value }))} />
        <TextField label="Semester ID" value={form.semester_id} onChange={e => setForm(s => ({ ...s, semester_id: e.target.value }))} />
        <TextField label="Credits" value={form.credits} onChange={e => setForm(s => ({ ...s, credits: e.target.value }))} />
        <TextField label="Group (optional)" value={form.divisionName} onChange={e => setForm(s => ({ ...s, divisionName: e.target.value }))} />
        <Button variant="contained" onClick={createManual}>Create</Button>
      </Stack>

      <Divider sx={{ my: 3 }} />

      <h3>Auto-Allocate</h3>
      <Stack direction="row" gap={2} flexWrap="wrap">
        <TextField label="Subject ID" value={autoForm.subjectId} onChange={e => setAutoForm(s => ({ ...s, subjectId: e.target.value }))} />
        <TextField label="Year ID" value={autoForm.yearId} onChange={e => setAutoForm(s => ({ ...s, yearId: e.target.value }))} />
        <TextField label="Semester ID" value={autoForm.semesterId} onChange={e => setAutoForm(s => ({ ...s, semesterId: e.target.value }))} />
        <TextField label="Teacher ID" value={autoForm.teacherId} onChange={e => setAutoForm(s => ({ ...s, teacherId: e.target.value }))} />
        <TextField label="Total Students" value={autoForm.totalStudents} onChange={e => setAutoForm(s => ({ ...s, totalStudents: e.target.value }))} />
        <TextField label="Max Capacity" value={autoForm.maxCapacity} onChange={e => setAutoForm(s => ({ ...s, maxCapacity: e.target.value }))} />
        <Button variant="contained" onClick={autoAllocate}>Auto Allocate</Button>
      </Stack>

      <Snackbar open={toast.open} autoHideDuration={2800} onClose={() => setToast(t => ({ ...t, open: false }))}>
        <Alert severity={toast.sev}>{toast.msg}</Alert>
      </Snackbar>
    </Box>
  );
}

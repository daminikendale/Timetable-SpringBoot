import React, { useState } from 'react';
import { Box, Stack, TextField, Button, Snackbar, Alert } from '@mui/material';
import http from '../api/http';

export default function TeacherAllocations() {
  const [rows, setRows] = useState([{ subject_id: '', teacher_id: '', year_id: '' }]);
  const [toast, setToast] = useState({ open: false, msg: '', sev: 'success' });

  const addRow = () => setRows(r => [...r, { subject_id: '', teacher_id: '', year_id: '' }]);
  const removeRow = (idx) => setRows(r => r.filter((_, i) => i !== idx));
  const update = (idx, key, val) => setRows(r => r.map((row, i) => i === idx ? { ...row, [key]: val } : row));

  const submit = async () => {
    try {
      const payload = rows.map(r => ({
        subject_id: Number(r.subject_id),
        teacher_id: Number(r.teacher_id),
        year_id: Number(r.year_id)
      }));
      await http.post('/teacher-allocations/bulk', payload);
      setToast({ open: true, msg: 'Saved allocations', sev: 'success' });
      setRows([{ subject_id: '', teacher_id: '', year_id: '' }]);
    } catch {
      setToast({ open: true, msg: 'Save failed', sev: 'error' });
    }
  };

  return (
    <Box>
      <h2>Teacher Subject Allocations (Bulk)</h2>
      <Stack gap={2}>
        {rows.map((r, idx) => (
          <Stack key={idx} direction="row" gap={2} alignItems="center" flexWrap="wrap">
            <TextField label="Subject ID" value={r.subject_id} onChange={e => update(idx, 'subject_id', e.target.value)} />
            <TextField label="Teacher ID" value={r.teacher_id} onChange={e => update(idx, 'teacher_id', e.target.value)} />
            <TextField label="Year ID" value={r.year_id} onChange={e => update(idx, 'year_id', e.target.value)} />
            <Button color="error" onClick={() => removeRow(idx)}>Remove</Button>
          </Stack>
        ))}
      </Stack>
      <Stack direction="row" gap={2} mt={2}>
        <Button variant="outlined" onClick={addRow}>Add Row</Button>
        <Button variant="contained" onClick={submit}>Submit</Button>
      </Stack>

      <Snackbar open={toast.open} autoHideDuration={2800} onClose={() => setToast(t => ({ ...t, open: false }))}>
        <Alert severity={toast.sev}>{toast.msg}</Alert>
      </Snackbar>
    </Box>
  );
}

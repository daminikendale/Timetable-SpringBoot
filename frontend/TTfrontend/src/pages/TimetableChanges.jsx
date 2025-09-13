import React, { useState, useEffect } from 'react';
import {
  Box, Button, Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, Stack, IconButton, Tooltip, Snackbar, Alert,
  FormControl, InputLabel, Select, MenuItem, Typography
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import http from '../api/http';
import { DataGrid } from '@mui/x-data-grid';

export default function TimetableChanges() {
  // Form State
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState({ type: 'TEMPORARY' });
  const [toast, setToast] = useState({ open: false, msg: '', sev: 'success' });

  // Data for Form Dropdowns
  const [divisions, setDivisions] = useState([]);
  const [teachers, setTeachers] = useState([]);
  const [subjects, setSubjects] = useState([]);

  // Data for Displaying Timetable
  const [effectiveDate, setEffectiveDate] = useState(new Date().toISOString().split('T')[0]);
  const [selectedDivisionId, setSelectedDivisionId] = useState('');
  const [effectiveTimetable, setEffectiveTimetable] = useState([]);
  const [loading, setLoading] = useState(false);

  // Load initial data for dropdowns
  useEffect(() => {
    const loadData = async () => {
      try {
        const [divRes, tRes, subRes] = await Promise.all([
          http.get('/divisions'),
          http.get('/teachers'),
          http.get('/subjects/dto')
        ]);
        setDivisions(divRes.data);
        setTeachers(tRes.data);
        setSubjects(subRes.data);
        if (divRes.data.length > 0) {
          setSelectedDivisionId(divRes.data[0].id);
        }
      } catch {
        setToast({ open: true, msg: 'Failed to load initial data', sev: 'error' });
      }
    };
    loadData();
  }, []);

  // Fetch effective timetable when date or division changes
  useEffect(() => {
    if (selectedDivisionId && effectiveDate) {
      const fetchEffective = async () => {
        setLoading(true);
        try {
          const res = await http.get(`/timetable-changes/effective?divisionId=${selectedDivisionId}&date=${effectiveDate}`);
          setEffectiveTimetable(res.data);
        } catch {
          setToast({ open: true, msg: 'Failed to fetch effective timetable', sev: 'error' });
          setEffectiveTimetable([]);
        } finally {
          setLoading(false);
        }
      };
      fetchEffective();
    }
  }, [selectedDivisionId, effectiveDate]);

  const handleSubmit = async () => {
    try {
      const payload = { ...form };
      // Ensure IDs are numbers if they exist
      ['divisionId', 'subjectId', 'fromTeacherId', 'toTeacherId', 'classroomId'].forEach(key => {
        if (payload[key]) payload[key] = Number(payload[key]);
      });
      
      await http.post('/timetable-changes', payload);
      setToast({ open: true, msg: 'Change applied successfully!', sev: 'success' });
      setOpen(false);
      // Refetch the effective timetable to show the update
      if (form.divisionId === selectedDivisionId && form.dateFrom === effectiveDate) {
         // Trigger useEffect
        const currentId = selectedDivisionId;
        setSelectedDivisionId('');
        setSelectedDivisionId(currentId);
      }
    } catch (e) {
      setToast({ open: true, msg: `Failed to apply change: ${e.response?.data?.message || e.message}`, sev: 'error' });
    }
  };

  const timetableColumns = [
    { field: 'id', headerName: 'ID', width: 70 },
    { field: 'timeSlot', headerName: 'Time Slot', flex: 1, valueGetter: (params) => `${params.row.timeSlot?.startTime} - ${params.row.timeSlot?.endTime}` },
    { field: 'subject', headerName: 'Subject', flex: 1, valueGetter: (params) => params.row.subject?.name || 'N/A' },
    { field: 'teacher', headerName: 'Teacher', flex: 1, valueGetter: (params) => params.row.teacher?.name || 'N/A' },
    { field: 'classroom', headerName: 'Classroom', flex: 1, valueGetter: (params) => params.row.classroom?.name || 'N/A' },
    { field: 'isOverride', headerName: 'Is Temp', type: 'boolean', width: 100 },
  ];

  return (
    <Box sx={{ p: 3 }}>
      <Stack direction="row" justifyContent="space-between" alignItems="center" mb={2}>
        <Typography variant="h4">Timetable Change Management</Typography>
        <Button variant="contained" startIcon={<AddIcon />} onClick={() => setOpen(true)}>
          Apply Change
        </Button>
      </Stack>

      <Stack direction="row" spacing={2} mb={3} alignItems="center">
        <FormControl fullWidth>
          <InputLabel>Division</InputLabel>
          <Select value={selectedDivisionId} label="Division" onChange={(e) => setSelectedDivisionId(e.target.value)}>
            {divisions.map(d => <MenuItem key={d.id} value={d.id}>{d.name}</MenuItem>)}
          </Select>
        </FormControl>
        <TextField
          label="Effective Date"
          type="date"
          value={effectiveDate}
          onChange={(e) => setEffectiveDate(e.target.value)}
          InputLabelProps={{ shrink: true }}
          fullWidth
        />
      </Stack>

      <Typography variant="h5" gutterBottom>Effective Timetable for {effectiveDate}</Typography>
      <Box sx={{ height: 400, width: '100%' }}>
        <DataGrid
          rows={effectiveTimetable}
          columns={timetableColumns}
          loading={loading}
          getRowId={(row) => row.id}
          pageSizeOptions={[5, 10]}
        />
      </Box>

      {/* Change Request Modal Form */}
      <Dialog open={open} onClose={() => setOpen(false)} fullWidth maxWidth="sm">
        <DialogTitle>Apply Timetable Change</DialogTitle>
        <DialogContent dividers>
          <Stack spacing={2} mt={1}>
            <FormControl fullWidth>
              <InputLabel>Change Type</InputLabel>
              <Select value={form.type || 'TEMPORARY'} label="Change Type" onChange={e => setForm(s => ({ ...s, type: e.target.value }))}>
                <MenuItem value="TEMPORARY">Temporary</MenuItem>
                <MenuItem value="PERMANENT">Permanent</MenuItem>
              </Select>
            </FormControl>

            <FormControl fullWidth required>
              <InputLabel>Division</InputLabel>
              <Select value={form.divisionId || ''} label="Division" onChange={e => setForm(s => ({ ...s, divisionId: e.target.value }))}>
                {divisions.map(d => <MenuItem key={d.id} value={d.id}>{d.name}</MenuItem>)}
              </Select>
            </FormControl>

            {form.type === 'TEMPORARY' && (
              <Stack direction="row" spacing={2}>
                <TextField label="From Date" type="date" required fullWidth InputLabelProps={{ shrink: true }} onChange={e => setForm(s => ({ ...s, dateFrom: e.target.value }))} />
                <TextField label="To Date" type="date" fullWidth InputLabelProps={{ shrink: true }} onChange={e => setForm(s => ({ ...s, dateTo: e.target.value }))} />
              </Stack>
            )}
            
            <FormControl fullWidth>
              <InputLabel>Teacher to Replace (Optional)</InputLabel>
              <Select value={form.fromTeacherId || ''} label="Teacher to Replace (Optional)" onChange={e => setForm(s => ({ ...s, fromTeacherId: e.target.value }))}>
                 <MenuItem value=""><em>None</em></MenuItem>
                {teachers.map(t => <MenuItem key={t.id} value={t.id}>{t.name}</MenuItem>)}
              </Select>
            </FormControl>

            <FormControl fullWidth required>
              <InputLabel>New Teacher</InputLabel>
              <Select value={form.toTeacherId || ''} label="New Teacher" onChange={e => setForm(s => ({ ...s, toTeacherId: e.target.value }))}>
                {teachers.map(t => <MenuItem key={t.id} value={t.id}>{t.name}</MenuItem>)}
              </Select>
            </FormControl>

            <FormControl fullWidth>
              <InputLabel>Subject (Optional Filter)</InputLabel>
              <Select value={form.subjectId || ''} label="Subject (Optional Filter)" onChange={e => setForm(s => ({ ...s, subjectId: e.target.value }))}>
                 <MenuItem value=""><em>None</em></MenuItem>
                {subjects.map(s => <MenuItem key={s.id} value={s.id}>{s.name}</MenuItem>)}
              </Select>
            </FormControl>

          </Stack>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={handleSubmit}>Apply</Button>
        </DialogActions>
      </Dialog>
      
      <Snackbar open={toast.open} autoHideDuration={4000} onClose={() => setToast(t => ({ ...t, open: false }))}>
        <Alert onClose={() => setToast(t => ({ ...t, open: false }))} severity={toast.sev} sx={{ width: '100%' }}>
          {toast.msg}
        </Alert>
      </Snackbar>
    </Box>
  );
}

import React, { useEffect, useMemo, useState } from 'react';
import { DataGrid, GridToolbar } from '@mui/x-data-grid';
import {
  Box, Button, Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, Stack, IconButton, Tooltip, Snackbar, Alert, LinearProgress
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';

export default function BaseCrud({
  title,
  idField = 'id',
  columns,
  formFields,
  fetchList,                // () => Promise<Array>
  createItem,               // (payload) => Promise
  updateItem,               // (id, payload) => Promise | undefined
  deleteItem,               // (id) => Promise | undefined
  toPayload,                // (form) => payload
  fromRow,                  // (row) => form defaults
  enableToolbar = true      // show DataGrid toolbar with quick filter
}) {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState({});
  const [editingId, setEditingId] = useState(null);
  const [toast, setToast] = useState({ open: false, msg: '', sev: 'success' });
  const [error, setError] = useState('');

  const load = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await fetchList();
      setRows(Array.isArray(data) ? data : []);
    } catch (e) {
      setError('Failed to load data');
      setToast({ open: true, msg: 'Load failed', sev: 'error' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  const onAdd = () => {
    setEditingId(null);
    setForm({});
    setOpen(true);
  };

  const onEdit = (row) => {
    if (!updateItem) return;
    setEditingId(row[idField]);
    setForm(fromRow ? fromRow(row) : row);
    setOpen(true);
  };

  const onDelete = async (row) => {
    if (!deleteItem) return;
    if (!window.confirm('Delete this record?')) return;
    try {
      await deleteItem(row[idField]);
      setToast({ open: true, msg: 'Deleted', sev: 'success' });
      load();
    } catch {
      setToast({ open: true, msg: 'Delete failed', sev: 'error' });
    }
  };

  const onSubmit = async () => {
    try {
      const payload = toPayload ? toPayload(form) : form;
      if (editingId && updateItem) {
        await updateItem(editingId, payload);
        setToast({ open: true, msg: 'Updated', sev: 'success' });
      } else {
        await createItem(payload);
        setToast({ open: true, msg: 'Created', sev: 'success' });
      }
      setOpen(false);
      load();
    } catch {
      setToast({ open: true, msg: 'Save failed', sev: 'error' });
    }
  };

  const actionCol = useMemo(() => ({
    field: '_actions',
    headerName: 'Actions',
    width: 120,
    sortable: false,
    filterable: false,
    renderCell: (params) => (
      <Box>
        {updateItem && (
          <Tooltip title="Edit">
            <IconButton size="small" onClick={() => onEdit(params.row)}>
              <EditIcon fontSize="small" />
            </IconButton>
          </Tooltip>
        )}
        {deleteItem && (
          <Tooltip title="Delete">
            <IconButton size="small" color="error" onClick={() => onDelete(params.row)}>
              <DeleteOutlineIcon fontSize="small" />
            </IconButton>
          </Tooltip>
        )}
      </Box>
    )
  }), [updateItem, deleteItem]);

  return (
    <Box>
      <Stack direction="row" justifyContent="space-between" alignItems="center" mb={2}>
        <h2 style={{ margin: 0 }}>{title}</h2>
        <Button variant="contained" startIcon={<AddIcon />} onClick={onAdd}>
          Add New
        </Button>
      </Stack>

      {error && (
        <Alert severity="error" sx={{ mb: 1 }}>
          {error}
        </Alert>
      )}

      <div style={{ height: 520, width: '100%', background: '#fff' }}>
        <DataGrid
          rows={rows}
          getRowId={(r) => r[idField]}     // map your unique key; MUI requires a stable id
          columns={[...columns, actionCol]}
          loading={loading}
          pagination
          pageSizeOptions={[10, 25, 50]}
          disableRowSelectionOnClick
          slots={{
            toolbar: enableToolbar ? GridToolbar : undefined,
            loadingOverlay: LinearProgress
          }}
          slotProps={{
            toolbar: { showQuickFilter: true, quickFilterProps: { debounceMs: 300 } }
          }}
        />
      </div>

      <Dialog open={open} onClose={() => setOpen(false)} fullWidth maxWidth="sm">
        <DialogTitle>{editingId ? 'Edit' : 'Add'} {title}</DialogTitle>
        <DialogContent dividers>
          <Stack spacing={2} mt={1}>
            {formFields.map(f => (
              <TextField
                key={f.name}
                label={f.label}
                type={f.type || 'text'}
                value={form[f.name] ?? ''}
                onChange={e => setForm(s => ({ ...s, [f.name]: e.target.value }))}
                required={f.required}
                fullWidth
              />
            ))}
          </Stack>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={onSubmit}>Save</Button>
        </DialogActions>
      </Dialog>

      <Snackbar
        open={toast.open}
        autoHideDuration={2800}
        onClose={() => setToast(t => ({ ...t, open: false }))}
      >
        <Alert severity={toast.sev}>{toast.msg}</Alert>
      </Snackbar>
    </Box>
  );
}

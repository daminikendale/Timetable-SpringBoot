import React from 'react';
import { NavLink } from 'react-router-dom';
import DashboardCustomizeIcon from '@mui/icons-material/DashboardCustomize';
import SchoolIcon from '@mui/icons-material/School';
import BookIcon from '@mui/icons-material/Book';
import MeetingRoomIcon from '@mui/icons-material/MeetingRoom';
import ApartmentIcon from '@mui/icons-material/Apartment';
import GroupWorkIcon from '@mui/icons-material/GroupWork';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import AvTimerIcon from '@mui/icons-material/AvTimer';
import FreeBreakfastIcon from '@mui/icons-material/FreeBreakfast';
import MenuBookIcon from '@mui/icons-material/MenuBook';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import GroupAddIcon from '@mui/icons-material/GroupAdd';
import EditCalendarIcon from '@mui/icons-material/EditCalendar'; // <-- 1. Import the new icon

const links = [
  { to: '/teachers', label: 'Teachers', Icon: SchoolIcon },
  { to: '/subjects', label: 'Subjects', Icon: BookIcon },
  { to: '/classrooms', label: 'Classrooms', Icon: MeetingRoomIcon },
  { to: '/departments', label: 'Departments', Icon: ApartmentIcon },
  { to: '/divisions', label: 'Divisions', Icon: GroupWorkIcon },
  { to: '/semesters', label: 'Semesters', Icon: CalendarMonthIcon },
  { to: '/timeslots', label: 'Time Slots', Icon: AvTimerIcon },
  { to: '/breaks', label: 'Breaks', Icon: FreeBreakfastIcon },
  { to: '/tutorials', label: 'Tutorials', Icon: MenuBookIcon },
  { to: '/working-hours', label: 'Working Hours', Icon: AccessTimeIcon },
  { to: '/electives', label: 'Electives', Icon: DashboardCustomizeIcon },
  { to: '/teacher-allocations', label: 'Teacher Allocations', Icon: GroupAddIcon },
  { to: '/timetable-changes', label: 'Timetable Changes', Icon: EditCalendarIcon }, // <-- 2. Add the new link object consistently
];

export default function Sidebar() {
  return (
    <aside style={{ width: 240, background: '#101826', color: '#fff', padding: 16 }}>
      <div style={{ fontWeight: 700, marginBottom: 16, display: 'flex', alignItems: 'center', gap: 8 }}>
        <DashboardCustomizeIcon /> Timetable Admin
      </div>
      <nav style={{ display: 'grid', gap: 8 }}>
        {links.map(({ to, label, Icon }) => (
          <NavLink
            key={to}
            to={to}
            style={({ isActive }) => ({
              display: 'flex',
              alignItems: 'center',
              gap: 10,
              padding: '10px 12px',
              borderRadius: 8,
              color: '#e6eaf2',
              textDecoration: 'none',
              background: isActive ? '#1d2a44' : 'transparent'
            })}
          >
            <Icon fontSize="small" /> {label}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}

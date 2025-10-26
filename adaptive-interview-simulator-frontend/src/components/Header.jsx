// src/components/Header.jsx
import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import useAuth from '../hooks/useAuth';

export default function Header() {
  const { isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate('/');
  }

  return (
    <header style={hdr}>
      <div>
        <Link to="/" style={{ ...linkStyle, fontWeight: 700 }}>
          Adaptive Interview
        </Link>
      </div>
      <nav>
        {isAuthenticated ? (
          <>
            <Link to="/dashboard" style={linkStyle}>
              Dashboard
            </Link>
            <button onClick={handleLogout} style={btnStyle}>
              Sign out
            </button>
          </>
        ) : (
          <>
            <Link to="/login" style={linkStyle}>
              Sign in
            </Link>
            <Link to="/register" style={linkStyle}>
              Create account
            </Link>
          </>
        )}
      </nav>
    </header>
  );
}

const hdr = {
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center',
  padding: '12px 20px',
  borderBottom: '1px solid rgba(0,0,0,0.06)',
};
const linkStyle = { marginRight: 12, textDecoration: 'none', color: '#0f172a' };
const btnStyle = { padding: '6px 10px', borderRadius: 6 };

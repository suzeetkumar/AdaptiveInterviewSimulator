// src/pages/Home.jsx
import React from 'react';
import { Link } from 'react-router-dom';

export default function Home() {
  return (
    <div style={wrap}>
      <h1 style={{ marginBottom: 8 }}>Adaptive Interview Simulator</h1>
      <p style={{ marginBottom: 18 }}>
        Practice behavioral & technical interviews with adaptive follow-ups and live feedback.
      </p>

      <div style={{ display: 'flex', gap: 12 }}>
        <Link to="/login" style={anchorStyle}>
          Sign in
        </Link>
        <Link to="/register" style={anchorStyle}>
          Create account
        </Link>
        <Link to="/session/demo/run" style={anchorStyle}>
          Try demo
        </Link>
      </div>
    </div>
  );
}

const wrap = {
  minHeight: '100vh',
  display: 'flex',
  flexDirection: 'column',
  alignItems: 'center',
  justifyContent: 'center',
  background: '#f7fbff',
  fontFamily: 'system-ui, Arial, sans-serif',
};

const anchorStyle = {
  display: 'inline-block',
  padding: '10px 18px',
  borderRadius: 6,
  border: '1px solid rgba(0,0,0,0.12)',
  textDecoration: 'none',
  color: '#111827',
  background: '#fff',
};

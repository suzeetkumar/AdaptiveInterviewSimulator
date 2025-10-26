// src/pages/Register.jsx
import React, { useState } from 'react';
import api from '../api/axiosConfig';
import { useNavigate } from 'react-router-dom';

export default function Register() {
  const [form, setForm] = useState({ name: '', email: '', password: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  function onChange(e) {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  }

  function validate() {
    if (!form.name || form.name.length < 2) {
      setError('Enter your full name.');
      return false;
    }
    if (!form.email || !/\S+@\S+\.\S+/.test(form.email)) {
      setError('Enter a valid email.');
      return false;
    }
    if (!form.password || form.password.length < 6) {
      setError('Password must be at least 6 characters.');
      return false;
    }
    return true;
  }

  async function onSubmit(e) {
    e.preventDefault();
    setError('');
    setSuccess('');
    if (!validate()) return;
    setLoading(true);

    try {
      await api.post('/auth/register', form);
      setSuccess('Account created — please sign in.');
      // Optional: navigate to login after a short delay
      setTimeout(() => navigate('/login'), 900);
    } catch (err) {
      console.error('Register error:', err);
      setError(err?.response?.data || err?.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div style={boxWrap}>
      <form onSubmit={onSubmit} style={formStyle}>
        <h2>Create account</h2>
        <input name="name" placeholder="Full name" value={form.name} onChange={onChange} required />
        <input
          name="email"
          placeholder="Email"
          type="email"
          value={form.email}
          onChange={onChange}
          required
        />
        <input
          name="password"
          placeholder="Password"
          type="password"
          value={form.password}
          onChange={onChange}
          required
        />
        {error && <div style={{ color: 'crimson' }}>{String(error)}</div>}
        {success && <div style={{ color: 'green' }}>{success}</div>}
        <button type="submit" disabled={loading}>
          {loading ? 'Creating...' : 'Create account'}
        </button>
      </form>
    </div>
  );
}

const boxWrap = {
  minHeight: '100vh',
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  background: '#f8fafc',
};
const formStyle = {
  display: 'flex',
  flexDirection: 'column',
  gap: 10,
  padding: 24,
  borderRadius: 10,
  background: '#fff',
  width: 340,
  boxShadow: '0 6px 18px rgba(2,6,23,0.08)',
};

// src/pages/Login.jsx
import React, { useState } from 'react';
import api, { setAuthToken } from '../api/axiosConfig';
import { useNavigate } from 'react-router-dom';

export default function Login() {
  const [form, setForm] = useState({ email: '', password: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  function onChange(e) {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  }

  function validate() {
    if (!form.email || !/\S+@\S+\.\S+/.test(form.email)) {
      setError('Enter a valid email address.');
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
    if (!validate()) return;
    setLoading(true);

    try {
      const res = await api.post('/auth/login', form);
      // Accept either res.data.token or res.data.accessToken or res.data.jwt
      const token =
        res?.data?.token || res?.data?.accessToken || res?.data?.jwt || res?.data?.access_token;
      if (!token) {
        setError('Login succeeded but server did not return a token.');
        return;
      }
      localStorage.setItem('token', token);
      setAuthToken(token); // ensure axios uses it
      navigate('/dashboard');
    } catch (err) {
      console.error('Login error:', err);
      setError(err?.response?.data || err?.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div style={boxWrap}>
      <form onSubmit={onSubmit} style={formStyle}>
        <h2>Login</h2>
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
        <button type="submit" disabled={loading}>
          {loading ? 'Signing in...' : 'Sign in'}
        </button>
        <p style={{ fontSize: 12, marginTop: 8 }}>
          (Dev tip: run <code>localStorage.setItem('token','&lt;any&gt;')</code> to bypass auth
          while developing.)
        </p>
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

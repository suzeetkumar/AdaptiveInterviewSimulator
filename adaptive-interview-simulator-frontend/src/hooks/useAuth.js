// src/hooks/useAuth.js
import { useState, useEffect } from 'react';
import { setAuthToken } from '../api/axiosConfig';

export default function useAuth() {
  const [token, setToken] = useState(() => localStorage.getItem('token'));

  useEffect(() => {
    setAuthToken(token);
  }, [token]);

  function loginWithToken(t) {
    localStorage.setItem('token', t);
    setAuthToken(t);
    setToken(t);
  }

  function logout() {
    localStorage.removeItem('token');
    setAuthToken(null);
    setToken(null);
  }

  return {
    token,
    isAuthenticated: !!token,
    loginWithToken,
    logout,
  };
}

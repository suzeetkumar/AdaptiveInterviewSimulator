// src/api/authService.js
import api from './axiosConfig';

export async function register(payload) {
  // payload = { name, email, password }
  const resp = await api.post('/auth/register', payload);
  return resp.data;
}

export async function login(payload) {
  // payload = { email, password }
  const resp = await api.post('/auth/login', payload);
  // expected response: { token: "..." }
  return resp.data;
}

export function setToken(token) {
  if (!token) return;
  localStorage.setItem('token', token);
}

export function clearToken() {
  localStorage.removeItem('token');
}

export function getToken() {
  return localStorage.getItem('token');
}

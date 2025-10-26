// src/api/axiosConfig.js
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api', // <<-- adjust if your backend URL differs
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
  // withCredentials: false // enable if you use cookies
});

// helper to attach JWT
export function setAuthToken(token) {
  if (token) api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  else delete api.defaults.headers.common['Authorization'];
}

export default api;

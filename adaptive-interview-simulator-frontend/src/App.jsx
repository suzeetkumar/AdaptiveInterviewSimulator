import Header from './components/Header.jsx';
import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Dashboard from './pages/Dashboard.jsx';
import Login from './pages/Login.jsx';
import Register from './pages/Register.jsx';
import Home from './pages/Home.jsx';
import SessionRunner from './pages/SessionRunner.jsx';
import SessionSummary from './pages/SessionSummary.jsx';

function PrivateRoute({ children }) {
  const token = localStorage.getItem('token');
  return token ? children : <Navigate to="/login" replace />;
}

export default function App() {
  return (
    <Router>
      <Header />
      <Routes>
        {/* public */}
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        {/* protected */}
        <Route
          path="/dashboard"
          element={
            <PrivateRoute>
              <Dashboard />
            </PrivateRoute>
          }
        />
        <Route
          path="/session/:id/run"
          element={
            <PrivateRoute>
              <SessionRunner />
            </PrivateRoute>
          }
        />
        <Route
          path="/session/:id/summary"
          element={
            <PrivateRoute>
              <SessionSummary />
            </PrivateRoute>
          }
        />
      </Routes>
    </Router>
  );
}

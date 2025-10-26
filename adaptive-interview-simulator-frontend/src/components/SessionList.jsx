import React from 'react';
import './SessionList.css';

export default function SessionList({ sessions }) {
  return (
    <div className="session-list">
      <h2>Recent Sessions</h2>
      <table>
        <thead>
          <tr>
            <th>#</th>
            <th>Date</th>
            <th>Type</th>
            <th>Average Score</th>
          </tr>
        </thead>
        <tbody>
          {sessions.map((s, i) => (
            <tr key={s.id}>
              <td>{i + 1}</td>
              <td>{s.date}</td>
              <td>{s.type}</td>
              <td>{Math.round(s.avgScore)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

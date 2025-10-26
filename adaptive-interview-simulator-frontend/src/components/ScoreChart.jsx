import React from 'react';
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  Legend,
  CartesianGrid,
  ResponsiveContainer,
} from 'recharts';
import './ScoreChart.css';

export default function ScoreChart({ data }) {
  return (
    <div className="chart-container">
      <h2>Performance Trends</h2>
      <ResponsiveContainer width="100%" height={300}>
        <LineChart data={data}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="date" />
          <YAxis domain={[0, 100]} />
          <Tooltip />
          <Legend />
          <Line type="monotone" dataKey="avgScore" name="Overall" stroke="#2563eb" />
          <Line type="monotone" dataKey="avgContentScore" name="Content" stroke="#16a34a" />
          <Line type="monotone" dataKey="avgClarityScore" name="Clarity" stroke="#f59e0b" />
          <Line type="monotone" dataKey="avgConfidenceScore" name="Confidence" stroke="#dc2626" />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
}

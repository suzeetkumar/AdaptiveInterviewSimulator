import './StatsCard.css';

export default function StatsCard({ title, value, color }) {
  return (
    <div className="stats-card" style={{ borderLeftColor: color }}>
      <div className="stats-title">{title}</div>
      <div className="stats-value">{value}</div>
    </div>
  );
}

// src/components/SessionReportButton.jsx
import React from 'react';
import api from '../api/axiosConfig';

export default function SessionReportButton({ sessionId }) {
  const downloadReport = async () => {
    try {
      const res = await api.get(`/sessions/${sessionId}/report`, { responseType: 'blob' });
      const url = URL.createObjectURL(new Blob([res.data], { type: 'application/pdf' }));
      const a = document.createElement('a');
      a.href = url;
      a.download = `session_${sessionId}.pdf`;
      a.click();
      URL.revokeObjectURL(url);
    } catch (err) {
      console.error('Error downloading report:', err);
      alert('Could not download report. Check console for details.');
    }
  };

  return (
    <button onClick={downloadReport} style={{ padding: '8px 12px', borderRadius: 6 }}>
      📄 Download Report
    </button>
  );
}

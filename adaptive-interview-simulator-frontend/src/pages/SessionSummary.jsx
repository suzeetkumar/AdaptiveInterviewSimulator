import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import api from '../api/axiosConfig';

export default function SessionSummary() {
  const { id } = useParams();
  const [session, setSession] = useState(null);

  useEffect(() => {
    api
      .get(`/sessions/${id}`)
      .then((res) => setSession(res.data))
      .catch(() => alert('Failed to fetch session'));
  }, [id]);

  const downloadReport = async () => {
    try {
      const res = await api.get(`/sessions/${id}/report`, { responseType: 'blob' });
      const url = URL.createObjectURL(new Blob([res.data], { type: 'application/pdf' }));
      const a = document.createElement('a');
      a.href = url;
      a.download = `session_${id}.pdf`;
      a.click();
      URL.revokeObjectURL(url);
    } catch {
      alert('Failed to download report');
    }
  };

  if (!session) return <p>Loading...</p>;

  return (
    <div style={{ padding: 20 }}>
      <h2>Session Summary #{session.id}</h2>
      <p>
        <strong>Type:</strong> {session.type}
      </p>
      <p>
        <strong>Started:</strong> {session.startedAt}
      </p>

      <button onClick={downloadReport}>Download PDF</button>

      <h3 style={{ marginTop: 20 }}>Questions</h3>
      {session.questions?.map((q) => (
        <div key={q.id} style={{ border: '1px solid #eee', padding: 10, marginBottom: 8 }}>
          <p>
            <strong>Q{q.sequenceIndex}:</strong> {q.promptText}
          </p>
          {q.answers?.map((a) => (
            <div key={a.id} style={{ marginLeft: 15 }}>
              <p>
                <strong>Answer:</strong> {a.answerText}
              </p>
              {a.analysis && (
                <p>
                  Scores → Content: {a.analysis.contentScore}, Confidence:{' '}
                  {a.analysis.confidenceScore}
                  <br />
                  Feedback: {a.analysis.aiFeedback}
                </p>
              )}
            </div>
          ))}
        </div>
      ))}

      <Link to="/">Back to Dashboard</Link>
    </div>
  );
}

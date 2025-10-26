import React, { useEffect, useState, useRef } from 'react';

export default function LiveFeedback({ sessionId }) {
  const [feedback, setFeedback] = useState({});
  const wsRef = useRef(null);

  useEffect(() => {
    const socket = new WebSocket('ws://localhost:8080/ws/live');
    wsRef.current = socket;

    socket.onopen = () => {
      console.log('✅ Connected to live feedback WebSocket');
      socket.send(JSON.stringify({ type: 'register', sessionId }));
    };

    socket.onmessage = (event) => {
      try {
        const msg = JSON.parse(event.data);
        if (msg.type === 'live_update') setFeedback(msg);
      } catch (e) {
        console.warn('Invalid WS message', e);
      }
    };

    socket.onclose = () => console.log('❌ WebSocket closed');
    return () => socket.close();
  }, [sessionId]);

  return (
    <div style={{ padding: 10, border: '1px solid #ccc', borderRadius: 8 }}>
      <h3>Live Feedback</h3>
      <p>
        <strong>Transcript:</strong> {feedback.transcript || '...'}
      </p>
      <p>
        <strong>Filler Words:</strong> {feedback.fillerCount ?? 0}
      </p>
      <p>
        <strong>Content Score:</strong> {feedback.contentScore ?? '-'}
      </p>
      <p>
        <strong>Confidence:</strong> {feedback.confidenceScore ?? '-'}
      </p>
      {feedback.followUp && <p style={{ fontStyle: 'italic' }}>Follow-up: {feedback.followUp}</p>}
    </div>
  );
}

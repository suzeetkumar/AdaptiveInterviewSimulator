// src/pages/SessionRunner.jsx
import React, { useEffect, useState, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig';
import AudioRecorder from '../components/AudioRecorder';
import LiveFeedback from '../components/LiveFeedback';

export default function SessionRunner() {
  const { id } = useParams();
  const sessionId = id;
  const [question, setQuestion] = useState(null);
  const [sequenceIndex, setSequenceIndex] = useState(1);
  const [answerText, setAnswerText] = useState('');
  const [loading, setLoading] = useState(false);
  const wsRef = useRef(null);
  const navigate = useNavigate();

  // Load first question
  useEffect(() => {
    api
      .get(`/sessions/${sessionId}`)
      .then((res) => {
        const firstQ = res.data.questions?.[0];
        if (firstQ) {
          setQuestion(firstQ.promptText);
          setSequenceIndex(firstQ.sequenceIndex || 1);
        } else {
          alert('No questions found for this session.');
        }
      })
      .catch((err) => {
        console.error('Failed to load session', err);
        alert('Failed to load session.');
      });
  }, [sessionId]);

  // Initialize WebSocket for live feedback
  useEffect(() => {
    const socket = new WebSocket('ws://localhost:8080/ws/live');
    wsRef.current = socket;
    socket.onopen = () => console.log('✅ WebSocket connected');
    socket.onclose = () => console.log('❌ WebSocket closed');
    return () => socket.close();
  }, []);

  // Send live transcript to backend WebSocket (for real-time tone/feedback)
  const sendLiveTranscript = (text) => {
    if (!wsRef.current || wsRef.current.readyState !== WebSocket.OPEN) return;
    wsRef.current.send(JSON.stringify({ type: 'interim_transcript', sessionId, text }));
  };

  // Submit text answer
  const submitTextAnswer = async () => {
    if (!answerText.trim()) {
      alert('Please type an answer first.');
      return;
    }
    setLoading(true);
    try {
      const res = await api.post(`/sessions/${sessionId}/answer`, {
        answerText,
        sequenceIndex,
      });
      handleServerResponse(res.data);
      setAnswerText('');
    } catch (err) {
      console.error('Text submit failed:', err);
      alert('Submit failed. Check server logs.');
    } finally {
      setLoading(false);
    }
  };

  // Handle audio submission
  const handleAudioSubmit = async (blob) => {
    const fd = new FormData();
    fd.append('audioFile', blob, `answer_${Date.now()}.webm`);
    fd.append('sequenceIndex', sequenceIndex);
    setLoading(true);
    try {
      const res = await api.post(`/sessions/${sessionId}/answer`, fd, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      handleServerResponse(res.data);
    } catch (err) {
      console.error('Audio upload failed:', err);
      alert('Audio upload failed.');
    } finally {
      setLoading(false);
    }
  };

  // Handle next question or end of session
  const handleServerResponse = (data) => {
    if (!data) return;
    if (data.sessionEnded) {
      alert('Session complete!');
      navigate(`/session/${sessionId}/summary`);
    } else {
      setQuestion(data.nextPromptText);
      setSequenceIndex(data.nextSequenceIndex);
    }
  };

  return (
    <div style={{ padding: 20 }}>
      <h2>Interview Session #{sessionId}</h2>

      <div style={{ marginTop: 16 }}>
        <h3>Question {sequenceIndex}</h3>
        <p style={{ fontSize: 18, color: '#0f172a' }}>{question || 'Loading...'}</p>
      </div>

      <textarea
        rows="4"
        value={answerText}
        onChange={(e) => {
          setAnswerText(e.target.value);
          sendLiveTranscript(e.target.value);
        }}
        style={{
          width: '100%',
          marginTop: 10,
          padding: 10,
          borderRadius: 6,
          border: '1px solid #d1d5db',
        }}
        placeholder="Type your answer or speak below..."
      />

      <div style={{ marginTop: 12 }}>
        <button onClick={submitTextAnswer} disabled={loading}>
          {loading ? 'Submitting...' : 'Submit Text'}
        </button>
        <span style={{ marginLeft: 12 }}>
          <AudioRecorder onAudioSubmit={handleAudioSubmit} />
        </span>
      </div>

      <div style={{ marginTop: 24 }}>
        <LiveFeedback sessionId={sessionId} />
      </div>
    </div>
  );
}

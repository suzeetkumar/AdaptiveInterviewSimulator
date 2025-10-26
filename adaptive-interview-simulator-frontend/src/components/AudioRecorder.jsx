import React, { useState, useRef } from 'react';
import PropTypes from 'prop-types';

export default function AudioRecorder({ onAudioSubmit }) {
  const [recording, setRecording] = useState(false);
  const mediaRef = useRef(null);
  const recorderRef = useRef(null);
  const chunksRef = useRef([]);

  const start = async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      mediaRef.current = stream;
      const rec = new MediaRecorder(stream);
      recorderRef.current = rec;
      chunksRef.current = [];

      rec.ondataavailable = (e) => {
        if (e.data.size > 0) chunksRef.current.push(e.data);
      };

      rec.onstop = () => {
        const blob = new Blob(chunksRef.current, { type: 'audio/webm' });
        if (onAudioSubmit) onAudioSubmit(blob);
        stream.getTracks().forEach((t) => t.stop());
      };

      rec.start();
      setRecording(true);
    } catch {
      // Handle permission errors silently or show alert
      alert('Microphone permission denied or unavailable.');
    }
  };

  const stop = () => {
    setRecording(false);
    if (recorderRef.current && recorderRef.current.state !== 'inactive') {
      recorderRef.current.stop();
    }
  };

  return (
    <div>
      {recording ? (
        <button type="button" onClick={stop} style={{ background: '#e74c3c', color: 'white' }}>
          ⏹ Stop & Upload
        </button>
      ) : (
        <button type="button" onClick={start}>
          🎤 Start Recording
        </button>
      )}
    </div>
  );
}

// ✅ Prop validation (removes "missing in props validation" warning)
AudioRecorder.propTypes = {
  onAudioSubmit: PropTypes.func,
};

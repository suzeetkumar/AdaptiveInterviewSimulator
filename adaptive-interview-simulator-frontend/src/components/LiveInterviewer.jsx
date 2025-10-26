import React, { useEffect, useRef, useState } from 'react';

export default function LiveInterviewer({ sessionId }) {
  const [socket, setSocket] = useState(null);
  const [liveData, setLiveData] = useState({});
  const recognitionRef = useRef(null);

  useEffect(() => {
    const ws = new WebSocket(`ws://localhost:8080/ws/live/${sessionId}`);
    ws.onopen = () => console.log('ws open');
    ws.onmessage = (ev) => {
      const data = JSON.parse(ev.data);
      setLiveData(data);
    };
    setSocket(ws);
    return () => {
      ws.close();
    };
  }, [sessionId]);

  function startRecognition() {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (!SpeechRecognition) {
      alert('No browser speech API');
      return;
    }
    const r = new SpeechRecognition();
    r.continuous = true;
    r.interimResults = true;
    r.onresult = (ev) => {
      let interim = '';
      for (let i = ev.resultIndex; i < ev.results.length; ++i) {
        interim = ev.results[i][0].transcript;
        const payload = {
          type: 'interim_transcript',
          sessionId,
          text: interim,
        };
        socket.send(JSON.stringify(payload));
      }
    };
    r.start();
    recognitionRef.current = r;
  }

  function stopRecognition() {
    if (recognitionRef.current) recognitionRef.current.stop();
    // notify finalize
    socket.send(JSON.stringify({ type: 'finalize', sessionId }));
  }

  return (
    <div>
      <button onClick={startRecognition}>Start Live Answer</button>
      <button onClick={stopRecognition}>Stop & Submit</button>

      <div>
        <h4>Live transcript</h4>
        <div>{liveData.transcript}</div>
        <div>Filler: {liveData.fillerCount}</div>
        <div>Confidence: {liveData.confidenceScore}</div>
        <div>Content score (live): {liveData.contentScore}</div>
        {liveData.followUp && (
          <div>
            <strong>Interviewer:</strong> {liveData.followUp}
          </div>
        )}
      </div>
    </div>
  );
}

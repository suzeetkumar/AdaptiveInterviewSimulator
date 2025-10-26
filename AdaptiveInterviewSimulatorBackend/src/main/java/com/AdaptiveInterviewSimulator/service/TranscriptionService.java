package com.AdaptiveInterviewSimulator.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class TranscriptionService {

    public String transcribe(MultipartFile file) {
        // MVP: Fake transcription for now (real: Whisper/OpenAI API)
        if (file == null || file.isEmpty()) return "";
        try {
            return "[Transcribed speech placeholder]"; // replace with real API call later
        } catch (Exception e) {
            throw new RuntimeException("Transcription failed: " + e.getMessage());
        }
    }
}

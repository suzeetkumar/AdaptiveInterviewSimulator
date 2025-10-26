package com.AdaptiveInterviewSimulator.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class ToneAnalysisService {
    private final Random random = new Random();

    public Map<String, Object> analyzeTone(byte[] audioBytes) {
        // MVP: fake metrics; later extract pitch, volume, pauses
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("avgPitch", 160 + random.nextInt(30)); // Hz
        metrics.put("avgVolume", 0.7 + random.nextDouble() * 0.3);
        metrics.put("pauseCount", random.nextInt(5));
        metrics.put("speakingRateWPM", 120 + random.nextInt(30));
        metrics.put("confidenceScore", 60 + random.nextInt(30));
        return metrics;
    }
}

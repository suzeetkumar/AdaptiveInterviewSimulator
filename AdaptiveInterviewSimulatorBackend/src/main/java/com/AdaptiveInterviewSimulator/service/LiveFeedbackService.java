package com.AdaptiveInterviewSimulator.service;

import com.AdaptiveInterviewSimulator.ai.CohereService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles real-time interview feedback while user speaks.
 * Receives WebSocket messages like:
 *   { "type": "interim_transcript", "sessionId": "123", "text": "..." }
 *   { "type": "finalize", "sessionId": "123" }
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LiveFeedbackService {

    private final CohereService cohereService;            // For AI follow-ups
    private final SimpleFeedbackService quickHeuristics;  // Lightweight heuristics

    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, StringBuilder> transcripts = new ConcurrentHashMap<>();

    /**
     * Handle incoming WebSocket text messages.
     * @param session The websocket session of the user.
     * @param payload The raw JSON text payload from frontend.
     */
    public void handleIncoming(WebSocketSession session, String payload) {
        try {
            JsonNode root = mapper.readTree(payload);
            String type = root.path("type").asText();
            String sessionId = root.path("sessionId").asText();

            switch (type) {
                case "interim_transcript" -> handleInterim(session, sessionId, root);
                case "finalize" -> handleFinalize(session, sessionId);
                default -> log.warn("Unknown message type: {}", type);
            }

        } catch (Exception e) {
            log.error("Error handling WebSocket message", e);
            sendError(session, e.getMessage());
        }
    }

    private void handleInterim(WebSocketSession session, String sessionId, JsonNode root) throws Exception {
        String chunk = root.path("text").asText();
        transcripts.computeIfAbsent(sessionId, k -> new StringBuilder()).append(" ").append(chunk);
        String current = transcripts.get(sessionId).toString();

        // quick heuristic analysis
        int filler = quickHeuristics.countFillers(current);
        int contentScore = quickHeuristics.computeContentScore(current);
        int confidence = quickHeuristics.computeConfidenceScore(current);

        // Build response JSON
        var out = mapper.createObjectNode();
        out.put("type", "live_update");
        out.put("sessionId", sessionId);
        out.put("transcript", current);
        out.put("fillerCount", filler);
        out.put("contentScore", contentScore);
        out.put("confidenceScore", confidence);

        // occasionally ask Cohere for a follow-up
        if (current.length() > 120 && Math.random() < 0.15) {
            try {
                String followUp = cohereService.generateFollowUp(current, "currentQuestion");
                out.put("followUp", followUp);
            } catch (Exception ex) {
                log.warn("Cohere follow-up failed: {}", ex.getMessage());
            }
        }

        session.sendMessage(new TextMessage(out.toString()));
    }

    private void handleFinalize(WebSocketSession session, String sessionId) throws Exception {
        StringBuilder sb = transcripts.remove(sessionId);
        String sessionText = (sb != null) ? sb.toString() : "";
        var out = mapper.createObjectNode();
        out.put("type", "finalized");
        out.put("sessionId", sessionId);
        out.put("finalTranscript", sessionText);
        session.sendMessage(new TextMessage(out.toString()));
        log.info("Session {} finalized, transcript length={}", sessionId, sessionText.length());
    }

    private void sendError(WebSocketSession session, String msg) {
        try {
            var out = mapper.createObjectNode();
            out.put("type", "error");
            out.put("message", msg);
            session.sendMessage(new TextMessage(out.toString()));
        } catch (Exception ignored) {}
    }

    public void registerSession(WebSocketSession session) {
        log.info("WebSocket connected: {}", session.getId());
    }

    public void handleClose(WebSocketSession session) {
        log.info("WebSocket closed: {}", session.getId());
        // remove any dangling transcript if exists
        transcripts.values().removeIf(sb -> sb.toString().contains(session.getId()));
    }
}

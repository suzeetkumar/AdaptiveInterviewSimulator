package com.AdaptiveInterviewSimulator.websocket;

import com.AdaptiveInterviewSimulator.service.LiveFeedbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class LiveWebSocketHandler implements WebSocketHandler {

    private final LiveFeedbackService liveFeedbackService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("WebSocket connection established: {}", session.getId());
        liveFeedbackService.registerSession(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        liveFeedbackService.handleIncoming(session, message.getPayload().toString());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("WebSocket transport error: {}", exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("WebSocket closed: {} | {}", session.getId(), status);
        liveFeedbackService.handleClose(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}

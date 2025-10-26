package com.AdaptiveInterviewSimulator.config;

import com.AdaptiveInterviewSimulator.websocket.LiveWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final LiveWebSocketHandler liveWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(liveWebSocketHandler, "/ws/live")
                // allow any origin for local testing
                .setAllowedOrigins("*");
        System.out.println("✅ WebSocket endpoint registered at /ws/live");
    }
}

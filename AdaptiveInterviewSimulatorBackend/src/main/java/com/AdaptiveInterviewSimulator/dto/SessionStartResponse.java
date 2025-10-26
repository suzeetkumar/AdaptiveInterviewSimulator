package com.AdaptiveInterviewSimulator.dto;

import lombok.Data;

@Data
public class SessionStartResponse {
    private Long sessionId;
    private Integer sequenceIndex;
    private String promptText;

    public SessionStartResponse(Long id, Integer sequenceIndex, String promptText) {
    }
}

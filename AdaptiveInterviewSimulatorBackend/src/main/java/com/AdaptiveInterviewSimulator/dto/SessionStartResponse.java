package com.AdaptiveInterviewSimulator.dto;

import lombok.Data;

@Data
public class SessionStartResponse {
    private Long sessionId;
    private Integer sequenceIndex;
    private String promptText;
}

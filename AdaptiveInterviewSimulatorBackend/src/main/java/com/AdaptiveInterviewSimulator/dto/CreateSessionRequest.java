package com.AdaptiveInterviewSimulator.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class CreateSessionRequest {
    @NotNull
    private String type; // "behavioral" or "technical"
    private Integer questionCount = 5; // default
}

package com.AdaptiveInterviewSimulator.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class SubmitAnswerRequest {
    @NotNull
    private Integer sequenceIndex;
    @NotNull
    private String answerText;
}

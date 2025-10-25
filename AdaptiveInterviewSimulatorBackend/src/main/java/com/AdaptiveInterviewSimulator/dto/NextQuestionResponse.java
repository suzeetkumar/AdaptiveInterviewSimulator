package com.AdaptiveInterviewSimulator.dto;

import lombok.Data;

@Data
public class NextQuestionResponse {
    private boolean sessionEnded;
    private Integer nextSequenceIndex;
    private String nextPromptText;
    private AnswerAnalysisResponse analysis;
}

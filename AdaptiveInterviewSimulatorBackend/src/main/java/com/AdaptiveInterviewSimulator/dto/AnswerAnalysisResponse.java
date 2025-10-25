package com.AdaptiveInterviewSimulator.dto;

import lombok.Data;

@Data
public class AnswerAnalysisResponse {
    private String answerText;
    private Integer contentScore;
    private Integer clarityScore;
    private Integer confidenceScore;
    private Integer fillerCount;
    private String aiFeedback; // for Phase2 we put simple feedback text
}

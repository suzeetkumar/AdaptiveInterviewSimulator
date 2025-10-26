package com.AdaptiveInterviewSimulator.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SubmitAnswerRequest {
    @NotNull
    private Integer sequenceIndex;
    @NotNull
    private String answerText;
    private MultipartFile audioFile;


}

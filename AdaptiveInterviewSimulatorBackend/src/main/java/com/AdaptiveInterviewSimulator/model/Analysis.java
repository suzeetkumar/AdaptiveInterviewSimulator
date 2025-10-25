package com.AdaptiveInterviewSimulator.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "analysis")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Analysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id")
    private Answer answer;

    private Integer contentScore;
    private Integer clarityScore;
    private Integer confidenceScore;
    private Integer fillerCount;

    @Column(columnDefinition = "json")
    private String pauseMetrics;

    @Lob
    private String aiFeedback;

    @Lob
    private String rawAiResponse;

    private Instant createdAt = Instant.now();
}

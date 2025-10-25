package com.AdaptiveInterviewSimulator.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "answers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_question_id")
    private SessionQuestion sessionQuestion;

    @Lob
    private String answerText;

    private String audioPath; // null for Phase 2
    private Integer durationMs;

    private String status = "analysed"; // "pending" or "analysed"

    private Instant createdAt = Instant.now();

    @OneToOne(mappedBy = "answer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Analysis analysis;
}

package com.AdaptiveInterviewSimulator.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "session_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private Session session;

    private Long questionId; // nullable if AI-generated or custom

    @Lob
    private String promptText;

    private Integer sequenceIndex;

    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "sessionQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers = new ArrayList<>();
}

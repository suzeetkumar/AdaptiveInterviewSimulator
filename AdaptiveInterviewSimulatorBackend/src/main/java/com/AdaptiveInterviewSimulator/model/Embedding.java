package com.AdaptiveInterviewSimulator.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name="embeddings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Embedding {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String sourceType; // "rubric"|"answer"
    private Long sourceId;
    @Column(columnDefinition = "json")
    private String vector; // store JSON array string
    private Instant createdAt = Instant.now();
}

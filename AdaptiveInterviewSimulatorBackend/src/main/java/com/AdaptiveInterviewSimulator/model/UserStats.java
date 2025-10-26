package com.AdaptiveInterviewSimulator.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_stats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private LocalDate date;

    private Double avgScore;
    private Integer sessionsCompleted;

    // Optional: keep separate metric trends
    private Double avgContentScore;
    private Double avgClarityScore;
    private Double avgConfidenceScore;
}

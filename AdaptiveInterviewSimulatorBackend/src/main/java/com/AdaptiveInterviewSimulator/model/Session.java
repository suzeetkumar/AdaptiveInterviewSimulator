package com.AdaptiveInterviewSimulator.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String type; // behavioral / technical

    @Column(columnDefinition = "json")
    private String settings; // keep JSON string for flexible settings

    private Instant startedAt = Instant.now();
    private Instant endedAt;

    @Column(columnDefinition = "json")
    private String summary;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sequenceIndex ASC")
    @Builder.Default
    private List<SessionQuestion> questions = new ArrayList<>();
}

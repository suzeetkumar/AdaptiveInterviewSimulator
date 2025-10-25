package com.AdaptiveInterviewSimulator.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "question_bank")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionBank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;
    @Lob
    private String promptText;
    private Integer difficulty;

    @Column(columnDefinition = "json")
    private String tags;
}

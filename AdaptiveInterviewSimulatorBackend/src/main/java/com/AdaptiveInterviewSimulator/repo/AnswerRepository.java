package com.AdaptiveInterviewSimulator.repo;

import com.AdaptiveInterviewSimulator.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}

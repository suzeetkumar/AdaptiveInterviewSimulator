package com.AdaptiveInterviewSimulator.repo;

import com.AdaptiveInterviewSimulator.model.SessionQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SessionQuestionRepository extends JpaRepository<SessionQuestion, Long> {
    Optional<SessionQuestion> findBySessionIdAndSequenceIndex(Long sessionId, Integer sequenceIndex);
}

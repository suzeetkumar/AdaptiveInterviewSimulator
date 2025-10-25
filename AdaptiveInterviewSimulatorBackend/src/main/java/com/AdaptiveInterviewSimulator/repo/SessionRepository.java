package com.AdaptiveInterviewSimulator.repo;

import com.AdaptiveInterviewSimulator.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByUserIdOrderByStartedAtDesc(Long userId);
}

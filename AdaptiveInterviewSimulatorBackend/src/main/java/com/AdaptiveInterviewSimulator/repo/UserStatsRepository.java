package com.AdaptiveInterviewSimulator.repo;

import com.AdaptiveInterviewSimulator.model.UserStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserStatsRepository extends JpaRepository<UserStats, Long> {
    Optional<UserStats> findByUserIdAndDate(Long userId, LocalDate date);
    List<UserStats> findByUserIdOrderByDateAsc(Long userId);
}

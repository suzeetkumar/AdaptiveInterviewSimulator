package com.AdaptiveInterviewSimulator.service;

import com.AdaptiveInterviewSimulator.model.Session;
import com.AdaptiveInterviewSimulator.model.SessionQuestion;
import com.AdaptiveInterviewSimulator.model.Answer;
import com.AdaptiveInterviewSimulator.model.UserStats;
import com.AdaptiveInterviewSimulator.repo.UserStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final UserStatsRepository userStatsRepository;

    @Transactional
    public void updateUserStats(Session session) {
        if (session == null || session.getUserId() == null) return;

        Long userId = session.getUserId();
        LocalDate today = LocalDate.now();

        double totalContent = 0, totalClarity = 0, totalConfidence = 0;
        int answers = 0;
        for (SessionQuestion q : session.getQuestions()) {
            for (Answer a : q.getAnswers()) {
                if (a.getAnalysis() != null) {
                    totalContent += a.getAnalysis().getContentScore();
                    totalClarity += a.getAnalysis().getClarityScore();
                    totalConfidence += a.getAnalysis().getConfidenceScore();
                    answers++;
                }
            }
        }

        double avgContent = answers == 0 ? 0 : totalContent / answers;
        double avgClarity = answers == 0 ? 0 : totalClarity / answers;
        double avgConfidence = answers == 0 ? 0 : totalConfidence / answers;
        double overall = (avgContent + avgClarity + avgConfidence) / 3.0;

        // find or create today's stat
        UserStats stats = userStatsRepository.findByUserIdAndDate(userId, today)
                .orElse(UserStats.builder()
                        .userId(userId)
                        .date(today)
                        .sessionsCompleted(0)
                        .avgScore(0.0)
                        .avgContentScore(0.0)
                        .avgClarityScore(0.0)
                        .avgConfidenceScore(0.0)
                        .build());

        // weighted update
        int prevSessions = stats.getSessionsCompleted();
        stats.setSessionsCompleted(prevSessions + 1);
        stats.setAvgScore((stats.getAvgScore() * prevSessions + overall) / (prevSessions + 1));
        stats.setAvgContentScore((stats.getAvgContentScore() * prevSessions + avgContent) / (prevSessions + 1));
        stats.setAvgClarityScore((stats.getAvgClarityScore() * prevSessions + avgClarity) / (prevSessions + 1));
        stats.setAvgConfidenceScore((stats.getAvgConfidenceScore() * prevSessions + avgConfidence) / (prevSessions + 1));

        userStatsRepository.save(stats);
    }

    public List<UserStats> getUserStats(Long userId) {
        return userStatsRepository.findByUserIdOrderByDateAsc(userId);
    }
}

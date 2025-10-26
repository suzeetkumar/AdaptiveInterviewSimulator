package com.AdaptiveInterviewSimulator.web;

import com.AdaptiveInterviewSimulator.model.UserStats;
import com.AdaptiveInterviewSimulator.repo.UserRepository;
import com.AdaptiveInterviewSimulator.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final UserRepository userRepository;

    @GetMapping("/{id}/stats")
    public ResponseEntity<List<UserStats>> getStats(@PathVariable("id") Long id, Authentication auth) {
        String email = auth.getName();
        Long userId = userRepository.findByEmail(email).orElseThrow().getId();
        if (!userId.equals(id)) return ResponseEntity.status(403).build();

        List<UserStats> stats = analyticsService.getUserStats(id);
        return ResponseEntity.ok(stats);
    }
}

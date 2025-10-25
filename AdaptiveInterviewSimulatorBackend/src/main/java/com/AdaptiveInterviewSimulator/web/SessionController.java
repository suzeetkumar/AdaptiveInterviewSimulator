package com.AdaptiveInterviewSimulator.web;

import com.AdaptiveInterviewSimulator.dto.*;
import com.AdaptiveInterviewSimulator.model.Session;
import com.AdaptiveInterviewSimulator.repo.UserRepository;
import com.AdaptiveInterviewSimulator.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> createSession(@Valid @RequestBody CreateSessionRequest req, Authentication auth) {
        String email = auth.getName();
        Long userId = userRepository.findByEmail(email).orElseThrow().getId();
        SessionStartResponse resp = sessionService.createSession(userId, req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/{id}/answer")
    public ResponseEntity<?> submitAnswer(@PathVariable("id") Long id,
                                          @Valid @RequestBody SubmitAnswerRequest req,
                                          Authentication auth) {
        String email = auth.getName();
        Long userId = userRepository.findByEmail(email).orElseThrow().getId();
        NextQuestionResponse resp = sessionService.submitAnswer(userId, id, req);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSession(@PathVariable("id") Long id, Authentication auth) {
        String email = auth.getName();
        Long userId = userRepository.findByEmail(email).orElseThrow().getId();
        Session session = sessionService.getSession(userId, id);
        return ResponseEntity.ok(session);
    }
}

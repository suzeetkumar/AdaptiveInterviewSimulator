package com.AdaptiveInterviewSimulator.web;

import com.AdaptiveInterviewSimulator.dto.CreateSessionRequest;
import com.AdaptiveInterviewSimulator.dto.NextQuestionResponse;
import com.AdaptiveInterviewSimulator.dto.SessionStartResponse;
import com.AdaptiveInterviewSimulator.dto.SubmitAnswerRequest;
import com.AdaptiveInterviewSimulator.model.Session;
import com.AdaptiveInterviewSimulator.repo.UserRepository;
import com.AdaptiveInterviewSimulator.service.ReportService;
import com.AdaptiveInterviewSimulator.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final UserRepository userRepository;
    private final ReportService reportService;

    /**
     * Create a new interview session for the authenticated user.
     */
    @PostMapping
    public ResponseEntity<?> createSession(@Valid @RequestBody CreateSessionRequest req, Authentication auth) {
        try {
            Long userId = getUserIdFromAuth(auth);
            SessionStartResponse resp = sessionService.createSession(userId, req);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "unable_to_create_session", "message", e.getMessage()));
        }
    }

    /**
     * Submit an answer (text or audio) for a session question.
     * Accepts either JSON (answerText only) or multipart/form-data (audioFile + text).
     */
    @PostMapping(
            value = "/{id}/answer",
            consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE }
    )
    public ResponseEntity<?> submitAnswer(
            @PathVariable("id") Long sessionId,
            @RequestPart(value = "audioFile", required = false) MultipartFile audioFile,
            @RequestParam(value = "answerText", required = false) String answerText,
            @RequestParam(value = "sequenceIndex", required = false) Integer sequenceIndex,
            Authentication auth) {

        try {
            Long userId = getUserIdFromAuth(auth);

            if (sequenceIndex == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "missing_sequenceIndex",
                        "message", "sequenceIndex is required"
                ));
            }

            SubmitAnswerRequest req = new SubmitAnswerRequest();
            req.setAnswerText(answerText);
            req.setSequenceIndex(sequenceIndex);
            req.setAudioFile(audioFile);

            NextQuestionResponse resp = sessionService.submitAnswer(userId, sessionId, req);
            return ResponseEntity.ok(resp);

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "not_found", "message", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "forbidden", "message", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "io_error", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "internal_error", "message", e.getMessage()));
        }
    }

    /**
     * Fetch full session details (authenticated user's own session only).
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getSession(@PathVariable("id") Long id, Authentication auth) {
        try {
            Long userId = getUserIdFromAuth(auth);
            Session session = sessionService.getSession(userId, id);
            return ResponseEntity.ok(session);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "not_found", "message", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "forbidden", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "internal_error", "message", e.getMessage()));
        }
    }

    /**
     * Generate and download session report as PDF.
     */
    @GetMapping(value = "/{id}/report", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> getSessionReport(@PathVariable("id") Long id, Authentication auth) {
        try {
            Long userId = getUserIdFromAuth(auth);
            Session session = sessionService.getSession(userId, id);

            byte[] pdf = reportService.generateSessionPdf(session);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "session_" + id + ".pdf");

            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "not_found", "message", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "forbidden", "message", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "pdf_error", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "internal_error", "message", e.getMessage()));
        }
    }

    // Helper to extract userId from Authentication principal
    private Long getUserIdFromAuth(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            throw new SecurityException("Unauthenticated");
        }
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found"))
                .getId();
    }
}

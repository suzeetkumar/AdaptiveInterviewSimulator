package com.AdaptiveInterviewSimulator.service;

import com.AdaptiveInterviewSimulator.ai.CohereService;
import com.AdaptiveInterviewSimulator.dto.*;
import com.AdaptiveInterviewSimulator.model.*;
import com.AdaptiveInterviewSimulator.repo.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final SessionQuestionRepository sessionQuestionRepository;
    private final AnswerRepository answerRepository;
    private final AnalysisRepository analysisRepository;
    private final QuestionBankRepository questionBankRepository;
    private final SimpleFeedbackService feedbackService;
    private final CohereService cohereService;
    private final EmbeddingRepository embeddingRepository;
    private final ObjectMapper objectMapper;
    private final AnalyticsService analyticsService;
    private final TranscriptionService transcriptionService;
    private final StorageService storageService;
    private final ToneAnalysisService toneAnalysisService;

    @Transactional
    public SessionStartResponse createSession(Long userId, CreateSessionRequest req) {
        Session s = Session.builder()
                .userId(userId)
                .type(req.getType())
                .settings("{\"questionCount\":" + req.getQuestionCount() + "}")
                .build();

        List<QuestionBank> bank = questionBankRepository.findByCategory(req.getType());
        if (bank.isEmpty()) bank = questionBankRepository.findAll();

        Collections.shuffle(bank);
        int count = Math.min(req.getQuestionCount(), bank.size());
        for (int i = 0; i < count; i++) {
            QuestionBank q = bank.get(i);
            SessionQuestion sq = SessionQuestion.builder()
                    .session(s)
                    .questionId(q.getId())
                    .promptText(q.getPromptText())
                    .sequenceIndex(i + 1)
                    .build();
            s.getQuestions().add(sq);
        }

        Session saved = sessionRepository.save(s);
        SessionQuestion first = saved.getQuestions().get(0);

        return new SessionStartResponse(saved.getId(), first.getSequenceIndex(), first.getPromptText());
    }

    @Transactional
    public NextQuestionResponse submitAnswer(Long userId, Long sessionId, SubmitAnswerRequest req) throws IOException {
        Session s = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NoSuchElementException("Session not found"));
        if (!Objects.equals(s.getUserId(), userId))
            throw new SecurityException("Forbidden");

        SessionQuestion sq = sessionQuestionRepository
                .findBySessionIdAndSequenceIndex(sessionId, req.getSequenceIndex())
                .orElseThrow(() -> new NoSuchElementException("Question not found"));

        String transcript = req.getAnswerText();

        // ---- AUDIO HANDLING ----
        MultipartFile audioFile = req.getAudioFile();
        String savedPath = null;
        if (audioFile != null && !audioFile.isEmpty()) {
            savedPath = storageService.saveAudio(audioFile, s.getId(), sq.getSequenceIndex());
            if (transcript == null || transcript.isBlank()) {
                transcript = transcriptionService.transcribe(audioFile);
            }
        }

        // ---- CREATE ANSWER ----
        Answer a = Answer.builder()
                .sessionQuestion(sq)
                .answerText(transcript)
                .audioPath(savedPath)
                .status("analysed")
                .createdAt(Instant.now())
                .build();
        a = answerRepository.save(a);

        // ---- HEURISTIC FEEDBACK ----
        int fillerCount = feedbackService.countFillers(transcript);
        int contentScore = feedbackService.computeContentScore(transcript);
        int clarityScore = feedbackService.computeClarityScore(transcript, fillerCount);
        int confidenceScore = feedbackService.computeConfidenceScore(transcript);
        String feedbackText = feedbackService.generateSimpleFeedback(transcript, fillerCount);

        Analysis analysis = Analysis.builder()
                .answer(a)
                .contentScore(contentScore)
                .clarityScore(clarityScore)
                .confidenceScore(confidenceScore)
                .fillerCount(fillerCount)
                .aiFeedback(feedbackText)
                .rawAiResponse(null)
                .createdAt(Instant.now())
                .build();
        analysis = analysisRepository.save(analysis);
        a.setAnalysis(analysis);
        answerRepository.save(a);

        // ---- TONE ANALYSIS (if audio present) ----
        if (audioFile != null) {
            Map<String, Object> tone = toneAnalysisService.analyzeTone(audioFile.getBytes());
            confidenceScore = ((Number) tone.get("confidenceScore")).intValue();
            analysis.setPauseMetrics(objectMapper.writeValueAsString(tone));
            analysisRepository.save(analysis);
        }

        // ---- EMBEDDING ----
        try {
            List<Float> emb = cohereService.embed(transcript);
            if (emb != null && !emb.isEmpty()) {
                Embedding e = Embedding.builder()
                        .sourceType("answer")
                        .sourceId(a.getId())
                        .vector(objectMapper.writeValueAsString(emb))
                        .build();
                embeddingRepository.save(e);
            }
        } catch (Exception ignored) {}

        // ---- COHERE FEEDBACK ----
        try {
            String aiResp = cohereService.generateFeedback(transcript, sq.getPromptText(), Map.of("questionId", sq.getQuestionId()));
            if (aiResp != null && !aiResp.isBlank()) {
                JsonNode root = objectMapper.readTree(aiResp);
                analysis.setContentScore(root.path("content_score").asInt(contentScore));
                analysis.setClarityScore(root.path("clarity_score").asInt(clarityScore));
                analysis.setConfidenceScore(root.path("confidence_score").asInt(confidenceScore));
                analysis.setAiFeedback(root.path("ai_feedback").asText(feedbackText));
                analysis.setRawAiResponse(aiResp);
                analysisRepository.save(analysis);
                a.setAnalysis(analysis);
                answerRepository.save(a);
            }
        } catch (Exception ignored) {}

        // ---- FOLLOW-UP / NEXT QUESTION ----
        String followUp = null;
        try {
            followUp = cohereService.generateFollowUp(transcript, sq.getPromptText());
            if (followUp != null) followUp = followUp.trim();
        } catch (Exception ignored) {}

        int nextIndex = req.getSequenceIndex() + 1;
        boolean sessionEnded = (nextIndex > s.getQuestions().size());

        NextQuestionResponse resp = new NextQuestionResponse();
        AnswerAnalysisResponse ar = new AnswerAnalysisResponse();
        Analysis persisted = a.getAnalysis();
        ar.setAnswerText(a.getAnswerText());
        ar.setContentScore(persisted.getContentScore());
        ar.setClarityScore(persisted.getClarityScore());
        ar.setConfidenceScore(persisted.getConfidenceScore());
        ar.setFillerCount(persisted.getFillerCount());
        ar.setAiFeedback(persisted.getAiFeedback());
        resp.setAnalysis(ar);

        if (sessionEnded) {
            int total = 0, items = 0;
            for (SessionQuestion q : s.getQuestions()) {
                for (Answer ans : q.getAnswers()) {
                    if (ans.getAnalysis() != null && ans.getAnalysis().getContentScore() != null) {
                        total += ans.getAnalysis().getContentScore();
                        items++;
                    }
                }
            }
            int avg = items == 0 ? 0 : total / items;
            s.setEndedAt(Instant.now());
            s.setSummary("{\"avgContentScore\":" + avg + "}");
            sessionRepository.save(s);
            analyticsService.updateUserStats(s);

            resp.setSessionEnded(true);
            resp.setNextSequenceIndex(null);
            resp.setNextPromptText(null);
        } else {
            if (followUp != null && !followUp.isBlank()) {
                resp.setNextSequenceIndex(nextIndex);
                resp.setNextPromptText(followUp);
            } else {
                SessionQuestion nextQ = sessionQuestionRepository
                        .findBySessionIdAndSequenceIndex(sessionId, nextIndex)
                        .orElseThrow();
                resp.setNextSequenceIndex(nextQ.getSequenceIndex());
                resp.setNextPromptText(nextQ.getPromptText());
            }
            resp.setSessionEnded(false);
        }

        return resp;
    }

    public Session getSession(Long userId, Long sessionId) {
        Session s = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NoSuchElementException("Session not found"));
        if (!Objects.equals(s.getUserId(), userId))
            throw new SecurityException("Forbidden");
        s.getQuestions().size(); // force load
        return s;
    }
}

package com.AdaptiveInterviewSimulator.service;

import com.AdaptiveInterviewSimulator.dto.*;
import com.AdaptiveInterviewSimulator.model.*;
import com.AdaptiveInterviewSimulator.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public SessionStartResponse createSession(Long userId, CreateSessionRequest req) {
        // create session
        Session s = Session.builder()
                .userId(userId)
                .type(req.getType())
                .settings("{\"questionCount\":" + req.getQuestionCount() + "}")
                .build();

        // pick questions from question_bank by category
        List<QuestionBank> bank = questionBankRepository.findByCategory(req.getType());
        if (bank.isEmpty()) {
            // fallback: get all and filter
            bank = questionBankRepository.findAll();
        }

        // choose up to questionCount randomly or sequentially
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

        // return first question
        SessionQuestion first = saved.getQuestions().get(0);
        SessionStartResponse resp = new SessionStartResponse();
        resp.setSessionId(saved.getId());
        resp.setSequenceIndex(first.getSequenceIndex());
        resp.setPromptText(first.getPromptText());
        return resp;
    }

    @Transactional
    public NextQuestionResponse submitAnswer(Long userId, Long sessionId, SubmitAnswerRequest req) {
        // load session
        Session s = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NoSuchElementException("Session not found"));

        // check ownership
        if (!Objects.equals(s.getUserId(), userId)) throw new SecurityException("Forbidden");

        // find session question
        SessionQuestion sq = sessionQuestionRepository.findBySessionIdAndSequenceIndex(sessionId, req.getSequenceIndex())
                .orElseThrow(() -> new NoSuchElementException("Question not found"));

        // store answer
        Answer a = Answer.builder()
                .sessionQuestion(sq)
                .answerText(req.getAnswerText())
                .status("analysed")
                .build();
        a = answerRepository.save(a);

        // basic analysis
        int fillerCount = feedbackService.countFillers(req.getAnswerText());
        int contentScore = feedbackService.computeContentScore(req.getAnswerText());
        int clarityScore = feedbackService.computeClarityScore(req.getAnswerText(), fillerCount);
        int confidenceScore = feedbackService.computeConfidenceScore(req.getAnswerText());
        String feedbackText = feedbackService.generateSimpleFeedback(req.getAnswerText(), fillerCount);

        // persist analysis
        Analysis analysis = Analysis.builder()
                .answer(a)
                .contentScore(contentScore)
                .clarityScore(clarityScore)
                .confidenceScore(confidenceScore)
                .fillerCount(fillerCount)
                .aiFeedback(feedbackText)
                .rawAiResponse(null)
                .build();
        analysis = analysisRepository.save(analysis);

        // link analysis to answer and save
        a.setAnalysis(analysis);
        answerRepository.save(a);

        // determine next question
        int nextIndex = req.getSequenceIndex() + 1;
        boolean sessionEnded = (nextIndex > s.getQuestions().size());
        NextQuestionResponse resp = new NextQuestionResponse();
        AnswerAnalysisResponse ar = new AnswerAnalysisResponse();
        ar.setAnswerText(a.getAnswerText());
        ar.setContentScore(contentScore);
        ar.setClarityScore(clarityScore);
        ar.setConfidenceScore(confidenceScore);
        ar.setFillerCount(fillerCount);
        ar.setAiFeedback(feedbackText);
        resp.setAnalysis(ar);

        if (sessionEnded) {
            // finalize session summary (naive aggregation)
            int total = 0;
            int items = 0;
            for (SessionQuestion q : s.getQuestions()) {
                for (Answer ans : q.getAnswers()) {
                    if (ans.getAnalysis() != null) {
                        total += ans.getAnalysis().getContentScore();
                        items++;
                    }
                }
            }
            int avg = items == 0 ? 0 : total / items;
            s.setEndedAt(java.time.Instant.now());
            s.setSummary("{\"avgContentScore\":" + avg + "}");
            sessionRepository.save(s);

            resp.setSessionEnded(true);
            resp.setNextSequenceIndex(null);
            resp.setNextPromptText(null);
        } else {
            SessionQuestion nextQ = sessionQuestionRepository.findBySessionIdAndSequenceIndex(sessionId, nextIndex)
                    .orElseThrow();
            resp.setSessionEnded(false);
            resp.setNextSequenceIndex(nextQ.getSequenceIndex());
            resp.setNextPromptText(nextQ.getPromptText());
        }

        return resp;
    }

    public Session getSession(Long userId, Long sessionId) {
        Session s = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NoSuchElementException("Session not found"));
        if (!Objects.equals(s.getUserId(), userId)) throw new SecurityException("Forbidden");
        // to ensure questions loaded (could use fetch join)
        s.getQuestions().size();
        return s;
    }
}

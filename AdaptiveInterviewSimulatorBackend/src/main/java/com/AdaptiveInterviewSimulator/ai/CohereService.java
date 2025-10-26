package com.AdaptiveInterviewSimulator.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CohereService {

    private final WebClient webClient = WebClient.builder().build();

    @Value("${cohere.api.base-url}")
    private String baseUrl;

    @Value("${cohere.api.key}")
    private String apiKey;

    @Value("${cohere.model}")
    private String model;

    @Value("${cohere.embedding-model}")
    private String embeddingModel;

    @Value("${cohere.request.timeout-ms:15000}")
    private long timeoutMs;

    // Generate structured feedback (we expect JSON string back)
    public String generateFeedback(String transcript, String questionText, Map<String,Object> context) {
        String prompt = buildFeedbackPrompt(transcript, questionText, context);

        Map<String, Object> body = Map.of(
                "model", model,
                "prompt", prompt,
                "max_tokens", 300,
                "temperature", 0.2,
                "return_likelihoods", "NONE"
        );

        return webClient.post()
                .uri(baseUrl + "/generate")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofMillis(timeoutMs))
                .map(resp -> {
                    // Cohere returns `text` or `generations`; adapt parsing to response shape
                    Object text = ((Map)resp).get("text");
                    return text == null ? resp.toString() : text.toString();
                })
                .onErrorResume(e -> Mono.just("{\"error\":\"cohere_error\",\"message\":\"" + e.getMessage() + "\"}"))
                .block();
    }

    // Generate a short adaptive follow-up question
    public String generateFollowUp(String transcript, String questionText) {
        String prompt = buildFollowUpPrompt(transcript, questionText);
        Map<String, Object> body = Map.of(
                "model", model,
                "prompt", prompt,
                "max_tokens", 40,
                "temperature", 0.3
        );
        return webClient.post()
                .uri(baseUrl + "/generate")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofMillis(timeoutMs))
                .map(resp -> {
                    Object text = ((Map)resp).get("text");
                    return text == null ? resp.toString() : text.toString();
                })
                .onErrorResume(e -> Mono.just("Could you elaborate on that?"))
                .block();
    }

    // Get embeddings for a piece of text
    public List<Float> embed(String text) {
        Map<String, Object> body = Map.of(
                "model", embeddingModel,
                "texts", List.of(text)
        );

        Map resp = webClient.post()
                .uri(baseUrl + "/embed")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofMillis(timeoutMs))
                .block();

        // Parse response: resp["embeddings"][0]["embedding"] or resp["data"][0]["embedding"] depending on API
        if (resp == null) return List.of();

        // Try common shapes
        if (resp.containsKey("embeddings")) {
            Map first = ((List<Map>)resp.get("embeddings")).get(0);
            return (List<Float>) first.get("embedding");
        } else if (resp.containsKey("data")) {
            Map first = ((List<Map>)resp.get("data")).get(0);
            return (List<Float>) first.get("embedding");
        } else {
            return List.of();
        }
    }

    // PROMPT BUILDERS (simple)
    private String buildFeedbackPrompt(String transcript, String questionText, Map<String,Object> context) {
        // Ask model to return JSON with numeric scores 0-100 and a short feedback
        return """
        You are an expert interview coach. Given the question and the candidate's transcript, return a JSON object with
        keys: content_score (0-100), clarity_score (0-100), confidence_score (0-100), ai_feedback (short text), improvement_tips (list).
        Question: %s
        Transcript: %s
        Context: %s

        Output strictly valid JSON only.
        """.formatted(questionText, transcript, context == null ? "{}" : context.toString());
    }

    private String buildFollowUpPrompt(String transcript, String questionText) {
        return """
        You are an interviewer. Based on the candidate's transcript, write one concise follow-up question (max 20 words)
        that probes for impact or metrics. Return just the question text.
        Question: %s
        Transcript: %s
        """.formatted(questionText, transcript);
    }
}

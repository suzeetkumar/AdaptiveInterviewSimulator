package com.AdaptiveInterviewSimulator.service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SimpleFeedbackService {

    // common filler words (extend as needed)
    private static final Set<String> FILLERS = Set.of("um","uh","like","you know","so","actually","basically","right");

    public int countFillers(String text) {
        if (text == null) return 0;
        String lower = text.toLowerCase();
        int count = 0;
        for (String f : FILLERS) {
            // word boundary search
            Pattern p = Pattern.compile("\\b" + Pattern.quote(f) + "\\b");
            Matcher m = p.matcher(lower);
            while (m.find()) count++;
        }
        return count;
    }

    public int computeContentScore(String text) {
        if (text == null || text.isBlank()) return 0;
        int words = text.trim().split("\\s+").length;
        // naive: prefer answers between 30-150 words
        if (words < 30) return Math.max(30, (int)(words * 2)); // small reward
        if (words <= 150) return 70 + Math.min(30, (words - 30) * 1 / 4); // base 70 + small bonus
        // too long -> penalty
        return Math.max(40, 100 - (words - 150) / 5);
    }

    public int computeClarityScore(String text, int fillerCount) {
        if (text == null || text.isBlank()) return 0;
        int base = 80;
        base -= fillerCount * 6; // each filler reduces clarity
        int avgWordLen = averageWordLength(text);
        if (avgWordLen < 3) base -= 5;
        if (avgWordLen > 6) base += 3;
        return Math.max(20, Math.min(100, base));
    }

    public int computeConfidenceScore(String text) {
        if (text == null || text.isBlank()) return 30;
        // naive heuristic: presence of assertive verbs or numbers improves confidence
        int score = 50;
        if (text.matches(".*\\b(led|achieved|increased|decreased|reduced|delivered|implemented)\\b.*")) score += 20;
        if (text.matches(".*\\b\\d+%|\\d+\\b.*")) score += 10;
        return Math.max(10, Math.min(100, score));
    }

    private int averageWordLength(String text) {
        String[] words = text.trim().split("\\s+");
        int total = 0;
        for (String w : words) total += w.length();
        return total / Math.max(1, words.length);
    }

    public String generateSimpleFeedback(String text, int fillerCount) {
        StringBuilder sb = new StringBuilder();
        sb.append("Feedback: ");
        if (fillerCount > 2) sb.append("Reduce filler words (e.g. um, uh). ");
        int words = text.trim().split("\\s+").length;
        if (words < 30) sb.append("Try to add more detail with concrete examples. ");
        if (words > 180) sb.append("Be more concise; aim for clarity and structure. ");
        sb.append("Use STAR structure: Situation, Task, Action, Result.");
        return sb.toString();
    }
}

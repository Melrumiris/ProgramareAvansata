package org.bots;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LlmAIBot extends BotPlayer {

    public enum Difficulty {
        EASY("You are playing a trivia game. Answer the questions like an average person would, even if the answers may be wrong, concisely with just the one word answer, no explanation."),
        HARD("You are playing a competitive trivia game. Be precise. Respond with the exact correct answer only, no explanation, no punctuation beyond what is necessary.");

        private final String systemPrompt;

        Difficulty(String systemPrompt) {
            this.systemPrompt = systemPrompt;
        }

        public String getSystemPrompt() {
            return systemPrompt;
        }
    }

    private static final String GEMINI_ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

    private static final Pattern TEXT_PATTERN =
            Pattern.compile("\"text\"\\s*:\\s*\"([^\"\\\\n]+)\"");

    private final Difficulty difficulty;
    private final HttpClient httpClient;

    public LlmAIBot(String botName, String gameName, Difficulty difficulty) {
        super(botName, gameName);
        this.difficulty = difficulty;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    @Override
    protected String generateAnswer(String question) {
        String apiKey = System.getenv("GEMINI_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("[LlmAIBot " + botName + "] GEMINI_API_KEY env var not set — returning 'unknown'");
            return "unknown";
        }

        String requestBody = buildRequestBody(question);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GEMINI_ENDPOINT + apiKey))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(10))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("[LlmAIBot " + botName + "] Gemini HTTP " + response.statusCode());
                return "unknown";
            }

            return parseGeneratedText(response.body());

        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            System.err.println("[LlmAIBot " + botName + "] request failed: " + e.getMessage());
            return "unknown";
        }
    }

    private String buildRequestBody(String question) {
        String escapedSystemPrompt = escapeJson(difficulty.getSystemPrompt());
        String escapedQuestion     = escapeJson(question);

        return "{"
             + "\"system_instruction\":{"
             +   "\"parts\":[{\"text\":\"" + escapedSystemPrompt + "\"}]"
             + "},"
             + "\"contents\":["
             +   "{\"parts\":[{\"text\":\"" + escapedQuestion + "\"}]}"
             + "]"
             + "}";
    }

    private String parseGeneratedText(String responseBody) {
        Matcher matcher = TEXT_PATTERN.matcher(responseBody);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        System.err.println("[LlmAIBot " + botName + "] could not parse 'text' from Gemini response");
        return "unknown";
    }

    private static String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}

package org.bots;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class CustomAIBot extends BotPlayer {

    private static final ConcurrentHashMap<String, String> knowledgeBase = new ConcurrentHashMap<>();
    private static final Random RANDOM = new Random();

    public CustomAIBot(String botName, String gameName) {
        super(botName, gameName);
    }

    @Override
    protected String generateAnswer(String question) {
        String known = knowledgeBase.get(question);
        if (known != null) {
            return known;
        }
        List<String> vocab = globalVocabulary;
        if (!vocab.isEmpty()) {
            return vocab.get(RANDOM.nextInt(vocab.size()));
        }
        return "I don't know";
    }

    @Override
    protected void learn(String question, String serverFeedback) {
        int bracketOpen  = serverFeedback.indexOf('[');
        int bracketClose = serverFeedback.indexOf(']');
        if (bracketOpen < 0 || bracketClose <= bracketOpen) {
            return;
        }
        String bracketContent = serverFeedback.substring(bracketOpen + 1, bracketClose).trim();
        if (bracketContent.isEmpty()) {
            return;
        }
        String firstAnswer = bracketContent.split(",")[0].trim();
        knowledgeBase.put(question, firstAnswer);
    }
}

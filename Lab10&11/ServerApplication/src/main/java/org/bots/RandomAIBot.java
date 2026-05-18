package org.bots;

import java.util.List;
import java.util.Random;

public class RandomAIBot extends BotPlayer {

    private static final Random RANDOM = new Random();

    public RandomAIBot(String botName, String gameName) {
        super(botName, gameName);
    }

    @Override
    protected String generateAnswer(String question) {
        List<String> vocab = globalVocabulary;
        if (!vocab.isEmpty()) {
            return vocab.get(RANDOM.nextInt(vocab.size()));
        }
        return "I don't know";
    }
}

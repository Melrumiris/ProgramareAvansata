package org.bots;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BotPlayer implements Runnable {

    private static final String HOST = "localhost";
    private static final int PORT = 9999;
    private static final Random RANDOM = new Random();

    protected static final List<String> globalVocabulary = new CopyOnWriteArrayList<>();

    private static final AtomicBoolean vocabularyLoaded = new AtomicBoolean(false);

    protected final String botName;
    protected final String gameName;

    public BotPlayer(String botName, String gameName) {
        this.botName = botName;
        this.gameName = gameName;
    }

    @Override
    public final void run() {
        try (Socket socket = new Socket(HOST, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("\\register " + botName);
            out.println("\\login " + botName);
            out.println("\\getvocabulary");
            out.println("\\joingame " + gameName);

            boolean awaitingQuestion = false;
            String currentQuestion = null;
            String line;

            while ((line = in.readLine()) != null) {

                if (line.startsWith("VOCABULARY:[")) {
                    if (vocabularyLoaded.compareAndSet(false, true)) {
                        String content = line.substring("VOCABULARY:[".length());
                        if (content.endsWith("]")) {
                            content = content.substring(0, content.length() - 1);
                        }
                        for (String token : content.split(",")) {
                            String trimmed = token.trim();
                            if (!trimmed.isEmpty()) {
                                globalVocabulary.add(trimmed);
                            }
                        }
                    }
                    continue;
                }

                if (line.contains("--- Question")) {
                    awaitingQuestion = true;
                    continue;
                }

                if (awaitingQuestion && !line.isEmpty() && !line.startsWith("[")) {
                    currentQuestion = line;
                    awaitingQuestion = false;
                    final String questionSnapshot = currentQuestion;
                    Thread.startVirtualThread(() -> {
                        try {
                            Thread.sleep(500 + RANDOM.nextInt(1001));
                            out.println("\\answer " + generateAnswer(questionSnapshot));
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    });
                    continue;
                }

                if (line.startsWith("Wrong. The correct answer(s):") && currentQuestion != null) {
                    learn(currentQuestion, line);
                }
            }

        } catch (IOException e) {
            System.err.println("[Bot " + botName + "] connection error: " + e.getMessage());
        }
    }

    protected abstract String generateAnswer(String question);

    protected void learn(String question, String serverFeedback) {
        // no-op by default
    }
}

package org.bots;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class LoadTestScenario {

    private static final String GAME_NAME = "stresstest-" + System.currentTimeMillis();
    private static final int    BOT_COUNT      = 10_000;
    private static final long   JOIN_WINDOW_MS = 5_000;
    private static final long   SETUP_DELAY_MS = 1_000;

    private static final String HOST = "localhost";
    private static final int    PORT = 9999;

    public static void main(String[] args) throws InterruptedException {
        Thread.startVirtualThread(LoadTestScenario::runCoordinator);
        Thread.sleep(SETUP_DELAY_MS);

        System.out.println("[LoadTest] Spawning " + BOT_COUNT + " bots...");
        for (int i = 0; i < BOT_COUNT; i++) {
            String botName = "bot-" + i;
            BotPlayer bot = (i % 2 == 0)
                    ? new RandomAIBot(botName, GAME_NAME)
                    : new CustomAIBot(botName, GAME_NAME);
            Thread.startVirtualThread(bot);
            if (i % 50 == 0) { Thread.sleep(10); }
        }
        System.out.println("[LoadTest] All bots launched. Keeping JVM alive...");
        Thread.sleep(Long.MAX_VALUE);
    }

    private static void runCoordinator() {
        try (Socket socket = new Socket(HOST, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            sendAndLog(out, "\\register coordinator");
            readLine(in);

            sendAndLog(out, "\\login coordinator");
            readLine(in);

            sendAndLog(out, "\\opengame " + GAME_NAME);
            readLine(in);

            System.out.println("[Coordinator] Game '" + GAME_NAME + "' opened. Waiting "
                    + JOIN_WINDOW_MS + " ms for bots to join...");
            Thread.sleep(JOIN_WINDOW_MS);

            sendAndLog(out, "\\startgame " + GAME_NAME);
            System.out.println("[Coordinator] Game started.");

            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("[Coordinator] " + line);
            }

        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            System.err.println("[Coordinator] error: " + e.getMessage());
        }
    }

    private static void sendAndLog(PrintWriter out, String command) {
        System.out.println("[Coordinator] >>> " + command);
        out.println(command);
    }

    private static String readLine(BufferedReader in) {
        try {
            return in.readLine();
        } catch (IOException e) {
            return null;
        }
    }
}

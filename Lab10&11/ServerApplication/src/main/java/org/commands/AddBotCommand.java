package org.commands;

import org.bots.CustomAIBot;
import org.bots.LlmAIBot;
import org.bots.RandomAIBot;
import org.connections.ClientThread;

public class AddBotCommand implements Command {

    private final ClientThread client;
    private final String args;

    public AddBotCommand(ClientThread client, String args) {
        this.client = client;
        this.args = args;
    }

    @Override
    public void exec() {
        // Expected syntax: <gameName> <botName> <type> [difficulty]
        String[] parts = args.trim().split("\\s+", 4);
        if (parts.length < 3) {
            client.sendMessage("Usage: \\addbot <gameName> <botName> <type> [difficulty]");
            client.sendMessage("  Types: random, custom, llm");
            client.sendMessage("  Difficulty (llm only): easy, hard");
            return;
        }

        String gameName      = parts[0];
        String botName       = parts[1];
        String type          = parts[2].toLowerCase();
        String difficultyArg = parts.length == 4 ? parts[3].toLowerCase() : "easy";

        var openGame = client.server.getOpenGame(gameName);
        if (openGame == null) {
            client.sendMessage("[Error] Game not found or already running.");
            return;
        }

        if (openGame.getCreator() != client) {
            client.sendMessage("[Error] Only the game creator can add bots.");
            return;
        }

        Runnable bot;
        switch (type) {
            case "random" -> bot = new RandomAIBot(botName, gameName);
            case "custom" -> bot = new CustomAIBot(botName, gameName);
            case "llm" -> {
                LlmAIBot.Difficulty difficulty = "hard".equals(difficultyArg)
                        ? LlmAIBot.Difficulty.HARD
                        : LlmAIBot.Difficulty.EASY;
                bot = new LlmAIBot(botName, gameName, difficulty);
            }
            default -> {
                client.sendMessage("[Error] Unknown bot type '" + type + "'. Valid types: random, custom, llm.");
                return;
            }
        }

        Thread.startVirtualThread(bot);
        client.sendMessage("[Server] Bot '" + botName + "' (" + type + ") spawned and will join shortly.");
    }
}

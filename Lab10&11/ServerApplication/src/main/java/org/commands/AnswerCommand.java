package org.commands;

import org.connections.ClientThread;

public class AnswerCommand implements Command {
    private final ClientThread client;
    private final String answer;

    public AnswerCommand(ClientThread client, String args) {
        this.client = client;
        this.answer = args.trim();
    }

    @Override
    public void exec() {
        if (client.loggedInPlayer == null) {
            client.sendMessage("[Error] You must \\login first.");
            return;
        }
        if (client.currentGame == null) {
            client.sendMessage("[Error] You are not in a running game.");
            return;
        }
        if (answer.isEmpty()) {
            client.sendMessage("Usage: \\answer <your answer>");
            return;
        }
        client.currentGame.submitAnswer(client, answer);
    }
}

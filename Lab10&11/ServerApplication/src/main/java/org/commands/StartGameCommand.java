package org.commands;

import org.connections.ClientThread;

public class StartGameCommand implements Command {
    private final ClientThread client;
    private final String gameName;

    public StartGameCommand(ClientThread client, String args) {
        this.client = client;
        this.gameName = args.trim();
    }

    @Override
    public void exec() {
        if (client.loggedInPlayer == null) {
            client.sendMessage("[Error] You must \\login first.");
            return;
        }
        if (gameName.isEmpty()) {
            client.sendMessage("Usage: \\startgame <game name>");
            return;
        }

        var game = client.server.getOpenGame(gameName);
        if (game == null) {
            client.sendMessage("[Error] No open game named '" + gameName + "' found.");
            return;
        }
        if (game.getCreator() != client) {
            client.sendMessage("[Error] Only the creator can start the game.");
            return;
        }

        client.server.startGame(gameName);
    }
}

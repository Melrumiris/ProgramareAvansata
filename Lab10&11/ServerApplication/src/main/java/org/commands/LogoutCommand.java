package org.commands;

import org.connections.ClientThread;

public class LogoutCommand implements Command {
    private final ClientThread client;

    public LogoutCommand(ClientThread client) {
        this.client = client;
    }

    @Override
    public void exec() {
        if (client.loggedInPlayer == null) {
            client.sendMessage("[Error] You are not logged in.");
            return;
        }
        if (client.currentGame != null) {
            client.sendMessage("[Error] You cannot logout while in a game.");
            return;
        }
        String username = client.loggedInPlayer.getUsername();
        client.loggedInPlayer = null;
        client.sendMessage("Goodbye, " + username + "!");
    }
}

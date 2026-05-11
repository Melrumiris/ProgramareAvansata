package org.commands;

import org.connections.ClientThread;

public class InvalidCommand implements Command {
    private final ClientThread client;
    private final String message;

    InvalidCommand(ClientThread client, String message) {
        this.client = client;
        this.message = message;
    }

    @Override
    public void exec() {
        client.sendMessage("[Error] " + message);
    }
}

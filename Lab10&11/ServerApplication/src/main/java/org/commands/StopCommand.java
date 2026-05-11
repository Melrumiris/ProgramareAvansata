package org.commands;

import org.connections.ClientThread;

public class StopCommand implements Command {
    private final ClientThread client;

    public StopCommand(ClientThread client) {
        this.client = client;
    }

    @Override
    public void exec() {
        client.sendMessage("Server stopped");
        client.server.stopServer();
    }
}

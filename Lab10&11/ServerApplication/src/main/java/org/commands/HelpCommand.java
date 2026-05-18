package org.commands;

import org.connections.ClientThread;

public class HelpCommand implements Command {
    private final ClientThread client;

    public HelpCommand(ClientThread client) {
        this.client = client;
    }

    @Override
    public void exec() {
        client.sendMessage("=== Available Commands ===");
        client.sendMessage("\\register <username>    - Create a new account");
        client.sendMessage("\\login <username>       - Log in with an existing account");
        client.sendMessage("\\logout                 - Log out of your account");
        client.sendMessage("\\opengame <name>        - Create a new game lobby");
        client.sendMessage("\\joingame <name>        - Join an open game lobby");
        client.sendMessage("\\startgame <name>       - Start the game you created");
        client.sendMessage("\\answer <your answer>   - Submit your answer during a game");
        client.sendMessage("\\search [player:<p>] [minscore:<n>] [game:<g>] - Search results with filters");
        client.sendMessage("\\stop                   - Stop the server");
        client.sendMessage("\\help                   - Show this help message");
    }
}

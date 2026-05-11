package org.commands;

import org.connections.ClientThread;
import org.database.dao.PlayerRepository;
import org.database.entity.Player;

public class LoginCommand implements Command {
    private final ClientThread client;
    private final String username;
    private final PlayerRepository playerRepository = new PlayerRepository();

    public LoginCommand(ClientThread client, String args) {
        this.client = client;
        this.username = args.trim();
    }

    @Override
    public void exec() {
        if (username.isEmpty()) {
            client.sendMessage("Usage: \\login <username>");
            return;
        }
        Player player = playerRepository.findByUsername(username);
        if (player == null) {
            client.sendMessage("[Error] No account found for '" + username + "'. Use \\register <username> to create one.");
            return;
        }
        client.loggedInPlayer = player;
        client.sendMessage("Welcome back, " + username + "!");
    }
}

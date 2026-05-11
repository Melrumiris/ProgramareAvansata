package org.commands;

import org.connections.ClientThread;
import org.database.dao.PlayerRepository;
import org.database.entity.Player;

public class RegisterCommand implements Command {
    private final ClientThread client;
    private final String username;
    private final PlayerRepository playerRepository = new PlayerRepository();

    public RegisterCommand(ClientThread client, String args) {
        this.client = client;
        this.username = args.trim();
    }

    @Override
    public void exec() {
        if (username.isEmpty()) {
            client.sendMessage("Usage: \\register <username>");
            return;
        }
        if (playerRepository.findByUsername(username) != null) {
            client.sendMessage("[Error] Username '" + username + "' is already taken. Try \\login " + username + ".");
            return;
        }
        Player player = new Player(username);
        playerRepository.create(player);
        client.loggedInPlayer = player;
        client.sendMessage("Account created. Welcome, " + username + "!");
    }
}

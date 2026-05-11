package org.commands;

import org.connections.ClientThread;
import org.database.dao.GameRepository;
import org.instances.RunningGame;

public class JoinGameCommand implements Command {
    private final ClientThread client;
    private final String gameName;
    private final GameRepository gameRepository = new GameRepository();

    public JoinGameCommand(ClientThread client, String args) {
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
            client.sendMessage("Usage: \\joingame <game name>");
            return;
        }
        if (client.currentGame != null) {
            client.sendMessage("[Error] You are already in a game.");
            return;
        }

        RunningGame game = client.server.getOpenGame(gameName);
        if (game == null) {
            client.sendMessage("[Error] No open game named '" + gameName + "' found.");
            return;
        }

        game.getGameEntity().getPlayers().add(client.loggedInPlayer);
        gameRepository.update(game.getGameEntity());
        game.addPlayer(client);

        client.sendMessage("You joined game '" + gameName + "'! Waiting for the creator to start.");
        game.getCreator().sendMessage(client.loggedInPlayer.getUsername() + " joined the game.");
    }
}

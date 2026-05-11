package org.commands;

import org.connections.ClientThread;
import org.database.dao.GameRepository;
import org.database.dao.QuestionRepository;
import org.database.entity.Game;
import org.database.entity.Question;
import org.instances.RunningGame;

import java.util.List;

public class OpenGameCommand implements Command {
    private final ClientThread client;
    private final String gameName;
    private final GameRepository gameRepository = new GameRepository();
    private final QuestionRepository questionRepository = new QuestionRepository();

    public OpenGameCommand(ClientThread client, String args) {
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
            client.sendMessage("Usage: \\opengame <game name>");
            return;
        }
        if (client.currentGame != null) {
            client.sendMessage("[Error] You are already in a game.");
            return;
        }
        if (client.server.getOpenGame(gameName) != null) {
            client.sendMessage("[Error] A game with that name already exists.");
            return;
        }

        List<Question> questions = questionRepository.findRandomN(RunningGame.QUESTIONS_PER_GAME);
        if (questions.isEmpty()) {
            client.sendMessage("[Error] No questions available. Load questions first.");
            return;
        }

        Game game = new Game(gameName);
        game.setQuestions(questions);
        game.getPlayers().add(client.loggedInPlayer);
        gameRepository.create(game);

        RunningGame runningGame = new RunningGame(gameName, game, client, questions);
        client.server.addOpenGame(runningGame);

        client.sendMessage("Game '" + gameName + "' created! Waiting for players. Use \\startgame " + gameName + " when ready.");
    }
}

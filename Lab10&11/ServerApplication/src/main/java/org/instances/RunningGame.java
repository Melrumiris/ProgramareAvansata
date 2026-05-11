package org.instances;

import org.connections.ClientThread;
import org.database.dao.GameRepository;
import org.database.dao.ResultRepository;
import org.database.entity.Game;
import org.database.entity.GameState;
import org.database.entity.Question;
import org.database.entity.Result;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RunningGame extends Thread {
    public static final int QUESTIONS_PER_GAME = 5;
    public static final long QUESTION_TIME_MS = 30_000;

    private final String name;
    private final Game gameEntity;
    private final ClientThread creator;
    private final List<ClientThread> players = Collections.synchronizedList(new ArrayList<>());
    private final List<Question> questions;

    private int currentQuestionIndex = 0;
    private final Map<UUID, Integer> scores = new ConcurrentHashMap<>();
    private final Map<UUID, Long> responseTimes = new ConcurrentHashMap<>();
    private final Set<UUID> answeredThisRound = Collections.synchronizedSet(new HashSet<>());

    private final Object roundLock = new Object();
    private long questionStartTime;

    private final GameRepository gameRepository = new GameRepository();
    private final ResultRepository resultRepository = new ResultRepository();

    public RunningGame(String name, Game gameEntity, ClientThread creator, List<Question> questions) {
        this.name = name;
        this.gameEntity = gameEntity;
        this.creator = creator;
        this.questions = new ArrayList<>(questions);
        addPlayer(creator);
    }

    public String getGameName() { return name; }
    public Game getGameEntity() { return gameEntity; }
    public ClientThread getCreator() { return creator; }

    public synchronized void addPlayer(ClientThread client) {
        players.add(client);
        client.currentGame = this;
        if (client.loggedInPlayer != null) {
            UUID id = client.loggedInPlayer.getId();
            scores.put(id, 0);
            responseTimes.put(id, 0L);
        }
    }

    public synchronized void submitAnswer(ClientThread client, String answer) {
        if (client.loggedInPlayer == null) {
            client.sendMessage("You must be logged in to answer.");
            return;
        }
        UUID playerId = client.loggedInPlayer.getId();
        if (answeredThisRound.contains(playerId)) {
            client.sendMessage("You already answered this question.");
            return;
        }
        answeredThisRound.add(playerId);

        long responseTime = System.currentTimeMillis() - questionStartTime;
        Question current = questions.get(currentQuestionIndex);

        if (current.isCorrect(answer)) {
            scores.merge(playerId, 1, Integer::sum);
            responseTimes.merge(playerId, responseTime, Long::sum);
            client.sendMessage("Correct! +1");
        } else {
            responseTimes.merge(playerId, responseTime, Long::sum);
            client.sendMessage("Wrong. The correct answer(s): " + current.getCorrectAnswers());
        }

        if (answeredThisRound.size() >= players.size()) {
            synchronized (roundLock) {
                roundLock.notifyAll();
            }
        }
    }

    private void broadcast(String message) {
        synchronized (players) {
            for (ClientThread client : players) {
                client.sendMessage(message);
            }
        }
    }

    @Override
    public void run() {
        broadcast("=== Game '" + name + "' is starting! ===");
        broadcast("You will have " + (QUESTION_TIME_MS / 1000) + " seconds per question.");

        for (int i = 0; i < questions.size(); i++) {
            currentQuestionIndex = i;
            answeredThisRound.clear();
            Question q = questions.get(i);
            questionStartTime = System.currentTimeMillis();

            broadcast("\n--- Question " + (i + 1) + "/" + questions.size() + " ---");
            broadcast(q.getText());
            broadcast("[You have " + (QUESTION_TIME_MS / 1000) + " seconds. Send: \\answer <your answer>]");

            synchronized (roundLock) {
                try {
                    roundLock.wait(QUESTION_TIME_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            broadcast("--- Time's up! ---");
            broadcastScores();
        }

        finishGame();
    }

    private void broadcastScores() {
        broadcast("\n--- Current Scores ---");
        synchronized (players) {
            for (ClientThread client : players) {
                if (client.loggedInPlayer != null) {
                    UUID id = client.loggedInPlayer.getId();
                    int score = scores.getOrDefault(id, 0);
                    broadcast(client.loggedInPlayer.getUsername() + ": " + score + " point(s)");
                }
            }
        }
    }

    private void finishGame() {
        broadcast("\n=== Game Over! ===");

        ClientThread winner = null;
        int bestScore = -1;
        long bestTime = Long.MAX_VALUE;

        synchronized (players) {
            for (ClientThread client : players) {
                if (client.loggedInPlayer == null) continue;
                UUID id = client.loggedInPlayer.getId();
                int score = scores.getOrDefault(id, 0);
                long time = responseTimes.getOrDefault(id, Long.MAX_VALUE);
                if (score > bestScore || (score == bestScore && time < bestTime)) {
                    bestScore = score;
                    bestTime = time;
                    winner = client;
                }
            }
        }

        gameEntity.setState(GameState.FINISHED);
        gameRepository.update(gameEntity);

        synchronized (players) {
            for (ClientThread client : players) {
                if (client.loggedInPlayer == null) continue;
                UUID id = client.loggedInPlayer.getId();
                int score = scores.getOrDefault(id, 0);
                long time = responseTimes.getOrDefault(id, 0L);
                Result result = new Result(client.loggedInPlayer, gameEntity, score, time);
                resultRepository.create(result);
                client.currentGame = null;
            }
        }

        if (winner != null) {
            broadcast("Winner: " + winner.loggedInPlayer.getUsername() +
                      " with " + bestScore + " point(s) and " + bestTime + "ms total response time!");
        } else {
            broadcast("No winner could be determined.");
        }

        creator.server.removeRunningGame(name);
    }
}

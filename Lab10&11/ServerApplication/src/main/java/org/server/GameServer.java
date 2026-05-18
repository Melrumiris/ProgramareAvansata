package org.server;

import org.connections.ClientThread;
import org.database.JPAUtil;
import org.instances.RunningGame;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GameServer {
    public static final int PORT = 9999;
    private volatile boolean running = true;
    private final ServerSocket serverSocket;
    private final ExecutorService pool = Executors.newFixedThreadPool(16);
    private final ConcurrentHashMap<String, RunningGame> openGames = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, RunningGame> runningGames = new ConcurrentHashMap<>();

    public GameServer() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Server is waiting on port " + PORT + "...");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            running = false;
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing server socket during shutdown: " + e.getMessage());
            }
            JPAUtil.close();
        }));
        while (running) {
            try {
                Socket socket = serverSocket.accept();
                pool.execute(new ClientThread(socket, this));
            } catch (IOException e) {
                if (running) {
                    System.err.println("Error accepting client connection: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        pool.shutdown();
        try {
            pool.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void addOpenGame(RunningGame game) {
        openGames.put(game.getGameName(), game);
    }

    public RunningGame getOpenGame(String name) {
        return openGames.get(name);
    }

    public void startGame(String name) {
        RunningGame game = openGames.remove(name);
        if (game != null) {
            runningGames.put(name, game);
            game.start();
        }
    }

    public void removeRunningGame(String name) {
        runningGames.remove(name);
    }

    public void stopServer() {
        running = false;
        try {
            serverSocket.close();
            System.out.println("[Server] closed");
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            new GameServer();
        } catch (Exception e) {
            System.err.println("Failed to start the server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

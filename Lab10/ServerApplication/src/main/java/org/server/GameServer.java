package org.server;

import org.connections.ClientThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class GameServer {
    public static final int PORT = 9999;
    private volatile boolean running = true;
    private final ServerSocket serverSocket;

    public GameServer() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Server is waiting on port " + PORT + "...");

        while (running) {
            try {
                Socket socket = serverSocket.accept();
                new ClientThread(socket, this).start();
            } catch (IOException e) {
                if (running){
                    System.err.println("Error accepting client connection: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
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

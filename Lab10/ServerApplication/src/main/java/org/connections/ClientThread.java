package org.connections;

import org.server.GameServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientThread extends Thread {
    private final Socket socket;
    private final GameServer server;

    public ClientThread(Socket socket, GameServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            System.out.println("[Client " + socket.getPort() + "] connected");
            String request;
            while ((request = in.readLine()) != null) {
                if ("stop".equals(request)) {
                    out.println("Server stopped");
                    server.stopServer();
                    break;
                } else {
                    out.println("Server received the request " + request);
                    System.out.println("[Client " + socket.getPort() + "] cmd: '" + request + "'");
                }
            }
            System.out.println("[Client " + socket.getPort() + "] disconnected");
        } catch (IOException e) {
            System.err.println("[Client " + socket.getPort() + "] Connection interrupted: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

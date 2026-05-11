package org.connections;

import org.commands.CommandFactory;
import org.database.entity.Player;
import org.instances.RunningGame;
import org.server.GameServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {
    private final Socket socket;
    public final GameServer server;
    private PrintWriter out;
    public volatile Player loggedInPlayer;
    public volatile RunningGame currentGame;

    public ClientThread(Socket socket, GameServer server) {
        this.socket = socket;
        this.server = server;
    }

    public synchronized void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
        ) {
            this.out = writer;
            System.out.println("[Client " + socket.getPort() + "] connected");
            String request;
            while ((request = in.readLine()) != null) {
                System.out.println("[Client " + socket.getPort() + "] cmd: '" + request + "'");
                CommandFactory.createCommand(request, this).exec();
            }
            System.out.println("[Client " + socket.getPort() + "] disconnected");
        } catch (IOException e) {
            System.err.println("[Client " + socket.getPort() + "] Connection interrupted: " + e.getMessage());
        } finally {
            this.out = null;
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

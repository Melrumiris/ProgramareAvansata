package org.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import org.io.ResponseStream;

public class GameClient {
    public static void main(String[] args) {
        String serverAddress = "127.0.0.1";
        int PORT = 9999;

        System.out.println("Make sure to '\\login' first");

        try (
                Socket socket = new Socket(serverAddress, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Scanner scanner = new Scanner(System.in)
        ) {
            ResponseStream responseThread = new ResponseStream(socket);

            while (true) {
                String command = scanner.nextLine();
                if ("\\exit".equals(command)) {
                    break;
                }
                out.println(command);
            }
        } catch (IOException e) {
            System.err.println("No server listening... " + e.getMessage());
        }
    }
}

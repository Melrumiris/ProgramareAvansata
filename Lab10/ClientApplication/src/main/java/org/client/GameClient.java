package org.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class GameClient {
    public static void main(String[] args) {
        String serverAddress = "127.0.0.1";
        int PORT = 9999;

        try (
                Socket socket = new Socket(serverAddress, PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Scanner scanner = new Scanner(System.in)
        ) {
            while (true) {
                String command = scanner.nextLine();
                if ("exit".equals(command)) {
                    break;
                }

                out.println(command);
                String response = in.readLine();
                System.out.println(response);

                if ("stop".equals(command)) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("No server listening... " + e.getMessage());
        }
    }
}

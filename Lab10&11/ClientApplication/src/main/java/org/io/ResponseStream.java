package org.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ResponseStream extends Thread{
    private BufferedReader in;

    public ResponseStream(Socket socket) throws IOException {
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.start();
    }

    @Override
    public void run() {
        try {
            String response;
            while ((response = in.readLine()) != null) {
                System.out.println(response);
            }
        } catch (IOException e) {
            System.err.println("Connection closed: " + e.getMessage());
        }
    }
}



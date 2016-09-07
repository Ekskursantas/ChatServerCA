/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.ProtocolStrings;

/**
 *
 * @author jens
 */
public class ClientHandler implements Runnable {

    private final Socket socket;
    private final PrintWriter writer;
    private final ChatServer server;
    private final Scanner input;

    public ClientHandler(Socket socket, Scanner input, PrintWriter writer, ChatServer server) {
        this.socket = socket;
        this.writer = writer;
        this.server = server;
        this.input = input;
    }

    public static ClientHandler handle(Socket socket, ChatServer server) throws IOException {
        Scanner input = new Scanner(socket.getInputStream());
        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        return new ClientHandler(socket, input, writer, server);
    }

    public void run() {
        try {
            while (true) {
                String message = input.nextLine();
                System.out.println("Received: " + message);
                if (message == null) {
                    continue;
                } else if (message.equals(ProtocolStrings.STOP)) {
                    break;
                }
                server.sendMulticast(message.toUpperCase());
            }
        } finally {
            try {
                writer.println(ProtocolStrings.STOP);//Echo the stop message back to the client for a nice closedown
                socket.close();
                server.removeHandler(this);
                System.out.println("Closed a Connection");
            } catch (IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void sendMessage(String message) {
        System.out.println("Sending " + message);
        writer.println(message);
        writer.flush();
    }
}

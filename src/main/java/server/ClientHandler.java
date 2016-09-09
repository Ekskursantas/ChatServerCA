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

    private String clientLogin;
    private int count = 0;
    private boolean emptyMessage;
    private String command;
    private String msg;
    private String receiver;

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
                emptyMessage = false;
                String message = input.nextLine();
                System.out.println("Received: " + message);
                if (message.contains(":")) {
                    String[] str = message.split(":");
                    stringLength(str);
                    if (command.equals(ProtocolStrings.LOGIN) && !emptyMessage) {
                        if (count == 0) {
                            if (!server.getLoginNames().contains(msg)) {
                                clientLogin = msg;
                                server.addToOnline(clientLogin);
                                server.onlineNow(this);
                                count++;
                            } else {
                                writer.println("LOGIN: " + msg + ", is already taken. Choose another one.");
                            }
                        } else {
                            writer.println("You already logged in as: " + clientLogin);
                        }

                    } else if (clientLogin == null) {
                        writer.println("You need to log in first. Write 'LOGIN:USERNAME'");
                    } else if (command.equals(ProtocolStrings.MSG) && !emptyMessage) {
                        if (str.length == 3) {
                            String receivers[] = receiver.split(",");
                            if (receivers.length == 1 && "".equals(receivers[0])) {
                                server.sendMulticast(this, msg); //message to all
                            } else {
                                server.writeTo(receivers, msg, this);
                            }
                        } else {
                            writer.println("The command structure is wrong try again 'MSG:RECEIVER:MESSAGE'");
                        }

                    } else if (command.equals(ProtocolStrings.LOGOUT)) {
                        break;
                    } //                    else if (server.getLoginNames().contains(command) && clientLogin != null) {
                    //                        server.writeTo(command, msg, this);
                    //                    } else if (command.contains(",")) {
                    //
                    //                        String[] users = command.split(",");
                    //                        server.writeToFew(users, msg, this);
                    //
                    //                    }
                    else {
                        writer.println("Command: '" + command + "' does not exist");
                    }
                } else if (clientLogin == null) {
                    writer.println("You need to log in first. Write 'LOGIN:USERNAME'");
//                    case ProtocolStrings.LOGOUT:
//                        if (msg != null) {
//                            writer.println("Wrong syntax. Should be empty after :");
//                            break;
//                        } else {
//                            
//                        }

                }
            }
        } finally {
            try {
                socket.close();
                server.removeHandler(this);
                server.removeFromChat(this);
                System.out.println("Closed a Connection");
            } catch (IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println(ex);
            }
        }
    }

    public void sendMessage(String message) {
        System.out.println("Sending " + message);
        writer.println(message);
        writer.println("");

        writer.flush();
    }

    public String getClientLogin() {
        return clientLogin;
    }

    private void stringLength(String[] str) {
        switch (str.length) {
            case 1:
                command = str[0];
                emptyMessage = true;
                break;
            case 2:
                command = str[0];
                msg = str[1];
                System.out.println("2");
                break;
            case 3:
                command = str[0];
                receiver = str[1];
                msg = str[2];
                System.out.println("3");
                break;
            default:
                writer.println("TOO MANY ARGUMENTS! MAX THREE ARGUMENTS 'TEXT:TEXT:TEXT'");
                break;
        }

    }

}

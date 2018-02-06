package com.lernskog.erik.zombieclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ZombieClientThread extends Thread {
    private ZombieClientActivity zombieClientActivity;
    private String command;

    public ZombieClientThread(ZombieClientActivity zombieClientActivity, String command) {
        this.zombieClientActivity = zombieClientActivity;
        this.command = command;
    }

    public void run() {
        try {
            if (command == "connect") {
                zombieClientActivity.zombieServerListenerThread = new ZombieServerListenerThread(zombieClientActivity);
                zombieClientActivity.socket = new Socket(zombieClientActivity.ip, zombieClientActivity.port);
                zombieClientActivity.to_server = new PrintWriter(new BufferedWriter(new OutputStreamWriter(zombieClientActivity.socket.getOutputStream())), true);
                zombieClientActivity.from_server = new BufferedReader(new InputStreamReader(zombieClientActivity.socket.getInputStream()));
                zombieClientActivity.zombieServerListenerThread = new ZombieServerListenerThread(zombieClientActivity);
                zombieClientActivity.zombieServerListenerThread.start();
                zombieClientActivity.print("connect");
            } else if (zombieClientActivity.to_server == null) {
                zombieClientActivity.print("Not connected");
            } else if (command == "register") {
                String send_command = zombieClientActivity.number + " REGISTER " + zombieClientActivity.user + " " + zombieClientActivity.password;
                zombieClientActivity.print(send_command);
                zombieClientActivity.to_server.println(send_command);
            } else if (command == "login") {
                String send_command = zombieClientActivity.number + " LOGIN " + zombieClientActivity.user + " " + zombieClientActivity.password;
                zombieClientActivity.print(send_command);
                zombieClientActivity.to_server.println(send_command);
            } else if (command == "logout") {
                String send_command = zombieClientActivity.number + " LOGOUT";
                zombieClientActivity.print(send_command);
                zombieClientActivity.to_server.println(send_command);
            } else if (command == "send_location") {
                String send_command = zombieClientActivity.number + " I-AM-AT " + zombieClientActivity.latitud + " " + zombieClientActivity.longitud;
                zombieClientActivity.print(send_command);
                zombieClientActivity.to_server.println(send_command);
            } else if (command == "list_visible_players") {
                String send_command = zombieClientActivity.number + " LIST-VISIBLE-PLAYERS";
                zombieClientActivity.print(send_command);
                zombieClientActivity.to_server.println(send_command);
            }
        } catch (IOException e) {
            e.printStackTrace();
            zombieClientActivity.print(e.getMessage());
        }
    }
}

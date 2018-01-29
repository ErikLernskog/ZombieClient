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
            } else if (command == "register") {
                zombieClientActivity.print(zombieClientActivity.number + " REGISTER " + zombieClientActivity.user + " " + zombieClientActivity.password);
                zombieClientActivity.to_server.println(zombieClientActivity.number + " REGISTER " + zombieClientActivity.user + " " + zombieClientActivity.password);
            } else if (command == "login") {
                zombieClientActivity.print(zombieClientActivity.number + " LOGIN " + zombieClientActivity.user + " " + zombieClientActivity.password);
                zombieClientActivity.to_server.println(zombieClientActivity.number + " LOGIN " + zombieClientActivity.user + " " + zombieClientActivity.password);
            } else if (command == "logout") {
                zombieClientActivity.print(zombieClientActivity.number + " LOGOUT");
                zombieClientActivity.to_server.println(zombieClientActivity.number + " LOGOUT");
            } else if (command == "send_location") {
                zombieClientActivity.print(zombieClientActivity.number + " I-AM-AT " + zombieClientActivity.latitud + " " + zombieClientActivity.longitud);
                zombieClientActivity.to_server.println(zombieClientActivity.number + " I-AM-AT " + zombieClientActivity.latitud + " " + zombieClientActivity.longitud);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

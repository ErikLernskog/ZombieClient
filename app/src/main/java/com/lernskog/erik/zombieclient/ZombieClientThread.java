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

    public ZombieClientThread(ZombieClientActivity zombieClientActivity) {
        this.zombieClientActivity = zombieClientActivity;
    }

    public void run() {
        try {
            zombieClientActivity.zombieServerListenerThread = new ZombieServerListenerThread(zombieClientActivity);
            zombieClientActivity.socket = new Socket("192.168.0.240", 2002);
            zombieClientActivity.to_server = new PrintWriter(new BufferedWriter(new OutputStreamWriter(zombieClientActivity.socket.getOutputStream())));
            zombieClientActivity.from_server = new BufferedReader(new InputStreamReader(zombieClientActivity.socket.getInputStream()));
            zombieClientActivity.zombieServerListenerThread = new ZombieServerListenerThread(zombieClientActivity);
            zombieClientActivity.zombieServerListenerThread.start();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}

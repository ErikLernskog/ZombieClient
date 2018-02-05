package com.lernskog.erik.zombieclient;


import java.io.IOException;

public class ZombieServerListenerThread extends Thread {
    private ZombieClientActivity zombieClientActivity;

    public ZombieServerListenerThread(ZombieClientActivity zombieClientActivity) {
        this.zombieClientActivity = zombieClientActivity;
    }

    public void run() {
        try {
            while (true) {
                String line_from_server = zombieClientActivity.from_server.readLine();
                if (line_from_server == null) {
                    zombieClientActivity.print("Nothing to read from server");
                } else {
                    zombieClientActivity.receive_message(line_from_server);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.lernskog.erik.zombieclient;


import java.io.IOException;

public class ZombieServerListenerThread extends Thread {
    public boolean readFromServer = true;
    private ZombieClientActivity zombieClientActivity;

    public ZombieServerListenerThread(ZombieClientActivity zombieClientActivity) {
        this.zombieClientActivity = zombieClientActivity;
    }

    public void run() {
        try {
            while (readFromServer) {
                String line_from_server = zombieClientActivity.from_server.readLine();
                if (line_from_server == null) {
                    zombieClientActivity.print("Nothing to read from server");
                    readFromServer = false;
                } else {
                    zombieClientActivity.receive_message(line_from_server);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

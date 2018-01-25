package com.lernskog.erik.zombieclient;

public class ZombieClientThread extends Thread {
    private ZombieClientActivity zombieClientActivity;

    public ZombieClientThread(ZombieClientActivity zombieClientActivity) {
        this.zombieClientActivity = zombieClientActivity;
    }

    public void run() {
        zombieClientActivity.zombieServerListenerThread = new ZombieServerListenerThread(zombieClientActivity);
    }
}

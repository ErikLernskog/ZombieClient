package com.lernskog.erik.zombieclient;


public class ZombieServerListenerThread extends Thread {
    private ZombieClientActivity zombieClientActivity;

    public ZombieServerListenerThread(ZombieClientActivity zombieClientActivity) {
        this.zombieClientActivity = zombieClientActivity;
    }

    public void run() {

    }
}

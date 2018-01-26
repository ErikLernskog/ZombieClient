package com.lernskog.erik.zombieclient;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ZombieClientActivity extends Activity {
    public ZombieClientThread zombieClientThread;
    public ZombieServerListenerThread zombieServerListenerThread;
    public Socket socket;
    public PrintWriter to_server;
    public BufferedReader from_server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zombie_client);
        zombieClientThread = new ZombieClientThread(this);
        zombieClientThread.start();
    }

    public void print(final String message) {
        Log.d("ZombieClientActivity", message);
    }
}

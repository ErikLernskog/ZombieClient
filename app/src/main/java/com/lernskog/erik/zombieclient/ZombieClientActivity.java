package com.lernskog.erik.zombieclient;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ZombieClientActivity extends FragmentActivity implements View.OnClickListener, OnMapReadyCallback {
    public ZombieServerListenerThread zombieServerListenerThread;
    public Socket socket;
    public PrintWriter to_server;
    public BufferedReader from_server;

    public Button connect_button;
    public Button register_button;
    public Button login_button;
    public Button logout_button;
    public Button send_location_button;

    public EditText port_edittext;
    public EditText ip_edittext;
    public EditText user_edittext;
    public EditText password_edittext;

    public TextView connection_state_textview;
    public TextView login_state_textview;
    public TextView status_state_textview;

    public EditText latitud_edittext;
    public EditText longitud_edittext;

    public String ip;
    public int port;
    public String user;
    public String password;
    public int number;
    public String longitud;
    public String latitud;
    private GoogleMap mMap;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        print("onMapReady");
        mMap = googleMap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        print("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zombie_client);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        connect_button = findViewById(R.id.connect_button);
        connect_button.setOnClickListener(this);
        register_button = findViewById(R.id.register_button);
        register_button.setOnClickListener(this);
        login_button = findViewById(R.id.login_button);
        login_button.setOnClickListener(this);
        logout_button = findViewById(R.id.logout_button);
        logout_button.setOnClickListener(this);
        send_location_button = findViewById(R.id.send_location_button);
        send_location_button.setOnClickListener(this);

        port_edittext = findViewById(R.id.port_edittext);
        ip_edittext = findViewById(R.id.ip_edittext);
        user_edittext = findViewById(R.id.user_edittext);
        password_edittext = findViewById(R.id.password_edittext);

        connection_state_textview = findViewById(R.id.connection_state_textview);
        login_state_textview = findViewById(R.id.login_state_textview);
        status_state_textview = findViewById(R.id.status_textview);

        longitud_edittext = findViewById(R.id.longitud_edittext);
        latitud_edittext = findViewById(R.id.latitud_edittext);

        number = 1;
    }

    @Override
    public void onClick(View v) {
        print("onClick");
        ZombieClientThread zombieClientThread;
        ip = ip_edittext.getText().toString();
        port = Integer.parseInt(port_edittext.getText().toString());
        user = user_edittext.getText().toString();
        password = password_edittext.getText().toString();
        longitud = longitud_edittext.getText().toString();
        latitud = latitud_edittext.getText().toString();
        if (v == connect_button) {
            print("connect");
            zombieClientThread = new ZombieClientThread(this, "connect");
            zombieClientThread.start();
        } else if (v == register_button) {
            print("register");
            zombieClientThread = new ZombieClientThread(this, "register");
            zombieClientThread.start();
        } else if (v == login_button) {
            print("login");
            zombieClientThread = new ZombieClientThread(this, "login");
            zombieClientThread.start();
        } else if (v == logout_button) {
            print("logout");
            zombieClientThread = new ZombieClientThread(this, "logout");
            zombieClientThread.start();
        } else if (v == send_location_button) {
            print("send_location");
            zombieClientThread = new ZombieClientThread(this, "send_location");
            zombieClientThread.start();
        }
        number = number + 1;
    }

    public void print(final String message) {
        Log.d("ZombieClientActivity", message);
        if (message.contains("ZombieServer")) {
            connection_state_textview.post(new Runnable() {
                @Override
                public void run() {
                    connection_state_textview.setText(message);
                }
            });
        }

        if (message.contains("WELCOME")) {
            login_state_textview.post(new Runnable() {
                @Override
                public void run() {
                    login_state_textview.setText("Logged In");
                }
            });
        }

        if (message.contains("ASYNC")) {
            login_state_textview.post(new Runnable() {
                @Override
                public void run() {
                    String[] async = message.split("[ ]+");
                    String name = async[2];
                    String type = async[3];
                    Double lat = Double.parseDouble(async[4]);
                    Double lng = Double.parseDouble(async[5]);

                    login_state_textview.setText(name);
                    LatLng me = new LatLng(lat, lng);
                    LatLng you = new LatLng(59.25403118133545, 15.247076153755189);
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(me).title(name + " " + type));
                    mMap.addMarker(new MarkerOptions().position(you).title("You Type"));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(me));

                    //mMap.setMyLocationEnabled(true);

                }
            });


            //Intent intent = new Intent(this, MapsActivity.class);
            //startActivity(intent);
        }
    }
}

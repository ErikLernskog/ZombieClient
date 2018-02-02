package com.lernskog.erik.zombieclient;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

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
    public String status;
    Map<String, Player> players;
    private GoogleMap googleMap;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private Boolean mRequestingLocationUpdates;
    private LocationRequest mLocationRequest;

    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        print("onMapReady");
        this.googleMap = googleMap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        print("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zombie_client);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
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

        players = new HashMap<String, Player>();
        user = user_edittext.getText().toString();
        status = status_state_textview.getText().toString();
        password = password_edittext.getText().toString();
        longitud = longitud_edittext.getText().toString();
        latitud = latitud_edittext.getText().toString();
        Player me = new Player(user, status, Double.parseDouble(latitud), Double.parseDouble(longitud));
        players.put(user, me);

        mFusedLocationClient = new FusedLocationProviderClient(this);
        mRequestingLocationUpdates = true;
        createLocationRequest();

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    print("onLocationResult");
                    Double lat = location.getLatitude();
                    Double lon = location.getLongitude();
                    Player player = players.get(user);
                    player.latitude = lat;
                    player.longitude = lon;
                    players.put(user, player);
                    latitud = lat.toString();
                    longitud = lon.toString();
                    latitud_edittext.setText(latitud);
                    longitud_edittext.setText(longitud);
                    send_location();
                }
            }
        };
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
            players.clear();
            Player me = new Player(user, status, Double.parseDouble(latitud), Double.parseDouble(longitud));
            players.put(user, me);
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
            //print("send_location");
            //zombieClientThread = new ZombieClientThread(this, "send_location");
            //zombieClientThread.start();
            print("list_visible_players");
            zombieClientThread = new ZombieClientThread(this, "list_visible_players");
            zombieClientThread.start();
        }
        number = number + 1;
    }

    public void send_location() {
        print("send_location");
        ZombieClientThread zombieClientThread = new ZombieClientThread(this, "send_location");
        zombieClientThread.start();
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

        if (message.contains(" PLAYER ")) {
            final ZombieClientActivity t = this;
            login_state_textview.post(new Runnable() {
                @Override
                public void run() {
                    String[] async = message.split("[ ]+");
                    String name = async[2];
                    String type = async[3];
                    Double latitude = Double.parseDouble(async[4]);
                    Double longitude = Double.parseDouble(async[5]);
                    Player currentplayer = new Player(name, type, latitude, longitude);
                    players.put(name, currentplayer);
                    login_state_textview.setText(name);
                    googleMap.clear();
                    for (String key : players.keySet()) {
                        Player player = players.get(key);
                        LatLng position = new LatLng(player.latitude, player.longitude);
                        googleMap.addMarker(new MarkerOptions().position(position).title(player.name + " " + player.type));
                        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                    }
                    if (ActivityCompat.checkSelfPermission(t, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(t, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    googleMap.setMyLocationEnabled(true);
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    googleMap.getUiSettings().setAllGesturesEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    googleMap.getUiSettings().setCompassEnabled(true);
                    googleMap.getUiSettings().setZoomControlsEnabled(true);
                }
            });
        }
    }
}

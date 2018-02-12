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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ZombieClientActivity extends FragmentActivity implements View.OnClickListener, OnMapReadyCallback {
    private final static String REQUESTING_LOCATION_UPDATES_KEY = "REQUESTING_LOCATION_UPDATES_KEY";
    public ZombieServerListenerThread zombieServerListenerThread;
    public Socket socket;
    public PrintWriter to_server;
    public BufferedReader from_server;
    public TextView status_state_textview;
    public String ip;
    public int port;
    public String user;
    public String password;
    public int number;
    public String longitud;
    public String latitud;
    public Boolean listAllPlayers;
    public String visibility;
    private Button connect_button;
    private Button register_button;
    private Button login_button;
    private Button logout_button;
    private Button send_location_button;
    private Button list_players_button;
    private Button set_visibility_button;
    private Button turn_button;
    private EditText port_edittext;
    private EditText ip_edittext;
    private EditText user_edittext;
    private EditText password_edittext;
    private EditText latitud_edittext;
    private EditText longitud_edittext;
    private EditText visibility_edittext;
    private TextView connection_state_textview;
    private TextView login_state_textview;
    private TextView register_state_textview;
    private TextView players_textview;
    private Map<String, Player> players;
    private GoogleMap googleMap;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private Boolean mRequestingLocationUpdates;
    private LocationRequest mLocationRequest;
    private Location myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        print("onCreate");
        super.onCreate(savedInstanceState);
        updateValuesFromBundle(savedInstanceState);

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
        list_players_button = findViewById(R.id.list_players_button);
        list_players_button.setOnClickListener(this);
        set_visibility_button = findViewById(R.id.visibility_button);
        set_visibility_button.setOnClickListener(this);
        turn_button = findViewById(R.id.turn_button);
        turn_button.setOnClickListener(this);

        port_edittext = findViewById(R.id.port_edittext);
        ip_edittext = findViewById(R.id.ip_edittext);
        user_edittext = findViewById(R.id.user_edittext);
        password_edittext = findViewById(R.id.password_edittext);
        visibility_edittext = findViewById(R.id.visibility_edittext);
        longitud_edittext = findViewById(R.id.longitud_edittext);
        latitud_edittext = findViewById(R.id.latitud_edittext);

        connection_state_textview = findViewById(R.id.connection_state_textview);
        register_state_textview = findViewById(R.id.register_state_textview);
        login_state_textview = findViewById(R.id.login_state_textview);
        status_state_textview = findViewById(R.id.status_textview);
        players_textview = findViewById(R.id.players_textview);

        number = 1;

        players = new HashMap<String, Player>();
        listAllPlayers = true;

        createLocationRequest();
        createLocationCallback();
    }

    @Override
    protected void onResume() {
        print("onResume");
        super.onResume();
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        update_values();
        send_command("connect");
    }

    @Override
    protected void onPause() {
        print("onPause");
        super.onPause();
        stopLocationUpdates();
        send_command("logout");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        print("onSaveInstanceState");
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        super.onSaveInstanceState(outState);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        print("updateValuesFromBundle");
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(REQUESTING_LOCATION_UPDATES_KEY);
            }
        }
    }

    protected void createLocationRequest() {
        print("createLocationRequest");
        mFusedLocationClient = new FusedLocationProviderClient(this);
        mRequestingLocationUpdates = true;
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationCallback() {
        print("onLocationResult");
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    myLocation = location;
                    Double lat = location.getLatitude();
                    Double lon = location.getLongitude();
                    latitud = lat.toString();
                    longitud = lon.toString();
                    latitud_edittext.setText(latitud);
                    longitud_edittext.setText(longitud);
                    send_command("send_location");
                }
            }
        };
    }

    private void startLocationUpdates() {
        print("startLocationUpdates");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    private void stopLocationUpdates() {
        print("stopLocationUpdates");
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        print("onMapReady");
        this.googleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.googleMap.setMyLocationEnabled(true);
        this.googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        this.googleMap.getUiSettings().setAllGesturesEnabled(true);
        this.googleMap.getUiSettings().setCompassEnabled(true);
        this.googleMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    public void onClick(View v) {
        print("onClick");
        update_values();
        if (v == connect_button) {
            send_command("connect");
        } else if (v == register_button) {
            send_command("register");
        } else if (v == login_button) {
            send_command("login");
        } else if (v == logout_button) {
            send_command("logout");
        } else if (v == send_location_button) {
            send_command("send_location");
        } else if (v == list_players_button) {
            send_command("list_visible_players");
        } else if (v == set_visibility_button) {
            send_command("set_visibility");
            send_command("list_visible_players");
        } else if (v == turn_button) {
            send_command("turn");
        }
    }

    public void update_values() {
        print("update_values");
        ip = ip_edittext.getText().toString();
        port = Integer.parseInt(port_edittext.getText().toString());
        user = user_edittext.getText().toString();
        password = password_edittext.getText().toString();
        longitud = longitud_edittext.getText().toString();
        latitud = latitud_edittext.getText().toString();
        visibility = visibility_edittext.getText().toString();
    }

    public void send_command(String command) {
        print(command);
        ZombieClientThread zombieClientThread = new ZombieClientThread(this, command);
        zombieClientThread.start();
        number = number + 1;
    }

    public void receive_message(final String message) {
        print(message);
        if (message.matches("(.*) PLAYER (.*) GONE")) {
            String[] playerInfo = message.split("[ ]+");
            String name = playerInfo[2];
            Player player = players.get(name);
            if (player == null) {
                player.marker.remove();
            }
            players.remove(name);
        } else if (message.contains(" PLAYER ")) {
            final ZombieClientActivity zombieClientActivity = this;
            status_state_textview.post(new Runnable() {
                @Override
                public void run() {
                    String[] playerInfo = message.split("[ ]+");
                    String name = playerInfo[2];
                    String type = playerInfo[3];
                    Double latitude = Double.parseDouble(playerInfo[4]);
                    Double longitude = Double.parseDouble(playerInfo[5]);

                    LatLng position = new LatLng(latitude, longitude);
                    Player player = players.get(name);

                    if (player == null) {
                        print("null");
                        Marker marker = googleMap.addMarker(new MarkerOptions().position(position).title(name + " " + type));
                        player = new Player(name, type, marker);
                    } else {
                        player.type = type;
                    }

                    if (player.type.equals("HUMAN")) {
                        player.marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.human));
                    } else {
                        player.marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.zombie));
                    }

                    if (player.name.equals(zombieClientActivity.user)) {
                        status_state_textview.setText(player.type);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                    } else {
                        float result[] = new float[1];
                        Location.distanceBetween(latitude, longitude, Double.parseDouble(zombieClientActivity.latitud), Double.parseDouble(zombieClientActivity.longitud), result);
                        player.marker.setSnippet("Distance " + String.valueOf(result[0]));
                    }
                    player.marker.setPosition(position);
                    players.put(name, player);
                }
            });
            if (listAllPlayers) {
                send_command("list_visible_players");
                listAllPlayers = false;
            }
        } else if (message.contains("ZombieServer")) {
            connection_state_textview.post(new Runnable() {
                @Override
                public void run() {
                    connection_state_textview.setText("Connected");
                    send_command("register");
                }
            });
        } else if (message.contains("WELCOME")) {
            login_state_textview.post(new Runnable() {
                @Override
                public void run() {
                    login_state_textview.setText("Logged In");
                    send_command("send_location");
                }
            });
            listAllPlayers = true;
        } else if (message.contains("REGISTER")) {
            login_state_textview.post(new Runnable() {
                @Override
                public void run() {
                    register_state_textview.setText("Registered");
                    send_command("login");
                }
            });
            listAllPlayers = true;
        } else if (message.contains(" GOODBYE")) {
            login_state_textview.post(new Runnable() {
                @Override
                public void run() {
                    login_state_textview.setText("Logged Out");
                    googleMap.clear();
                    players.clear();
                }
            });
        } else if (message.contains("VISIBLE-PLAYERS")) {
            players_textview.post(new Runnable() {
                @Override
                public void run() {
                    String[] visible = message.split("[ ]+");
                    String visability = visible[2];
                    String numberofplayers = visible[3];
                    print("visability " + visability + " numberofplayers " + numberofplayers);
                    players_textview.setText(numberofplayers);
                    visibility_edittext.setText(visability);
                    googleMap.clear();
                    players.clear();
                }
            });
        }
    }

    public void print(final String message) {
        Log.d("ZombieClientActivity", message);
    }
}

package com.lernskog.erik.zombieclient;

import com.google.android.gms.maps.model.Marker;

public class Player {
    public String name;
    public String type;
    public Marker marker;

    public Player(String name, String type, Marker marker) {
        this.name = name;
        this.type = type;
        this.marker = marker;
    }
}

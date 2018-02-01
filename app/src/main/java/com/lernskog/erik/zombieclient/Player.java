package com.lernskog.erik.zombieclient;

public class Player {
    public String name;
    public String type;
    public Double latitude;
    public Double longitude;

    public Player(String name, String type, Double latitude, Double longitude) {
        this.name = name;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}

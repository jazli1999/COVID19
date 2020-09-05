package com.bupt.sse.group7.covid19.model;

public class TrackPoint {
    public String getLocation() {
        return location;
    }

    private String date_time;
    private String location;
    private String description;

    public TrackPoint(String date_time, String location, String description) {
        this.date_time = date_time;
        this.location = location;
        this.description = description;
    }
}

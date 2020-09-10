package com.bupt.sse.group7.covid19.model;

public class BusTrack {
    String uid;
    int p_id;
    String name;
    String start;
    String end;
    String date_time;

    public BusTrack(String uid, int p_id, String name, String start, String end, String date_time) {
        this.uid = uid;
        this.p_id = p_id;
        this.name = name;
        this.start = start;
        this.end = end;
        this.date_time = date_time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getP_id() {
        return p_id;
    }

    public void setP_id(int p_id) {
        this.p_id = p_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }
}

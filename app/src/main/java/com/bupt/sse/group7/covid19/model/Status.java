package com.bupt.sse.group7.covid19.model;

public class Status {
    private String day;
    private String status;

    public Status(String day, String status) {
        this.day = day;
        this.status = status;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
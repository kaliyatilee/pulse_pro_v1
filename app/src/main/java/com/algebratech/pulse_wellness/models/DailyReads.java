package com.algebratech.pulse_wellness.models;

public class DailyReads {
    Integer id;
    String user;
    String points;
    String steps;
    String distance;
    String kcals;
    String date;
    Boolean sync;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getKcals() {
        return kcals;
    }

    public void setKcals(String kcals) {
        this.kcals = kcals;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean getSync() {
        return sync;
    }

    public void setSync(Boolean sync) {
        this.sync = sync;
    }
}

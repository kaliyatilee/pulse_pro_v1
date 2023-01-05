package com.algebratech.pulse_wellness.models;

public class ActivityListModel {
    private int id;
    private String activity_type;
    private String user;
    private String duration;
    private int steps;
    private String distance;
    private String average_pace;
    private String average_heartrate;
    private String kcals;
    private String date;
    private String map_path;
    private String map_image;
    private String camera_image;
    private Boolean sync;

    public ActivityListModel(
            String activity_type,
            String user,
            String duration,
            int steps,
            String distance,
            String average_pace,
            String average_heartrate,
            String kcals,
            String date,
            String map_path,
            String map_image,
            String camera_image,
            Boolean sync) {
        this.activity_type = activity_type;
        this.user = user;
        this.duration = duration;
        this.steps = steps;
        this.distance = distance;
        this.average_pace = average_pace;
        this.average_heartrate = average_heartrate;
        this.kcals = kcals;
        this.date = date;
        this.map_path = map_path;
        this.map_image = map_image;
        this.camera_image = camera_image;
        this.sync = sync;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getActivity_type() {
        return activity_type;
    }

    public void setActivity_type(String activity_type) {
        this.activity_type = activity_type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public String getAverage_heartrate() {
        return average_heartrate;
    }

    public void setAverage_heartrate(String average_heartrate) {
        this.average_heartrate = average_heartrate;
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

    public String getMap_path() {
        return map_path;
    }

    public void setMap_path(String map_path) {
        this.map_path = map_path;
    }

    public String getMap_image() {
        return map_image;
    }

    public void setMap_image(String map_image) {
        this.map_image = map_image;
    }

    public String getCamera_image() {
        return camera_image;
    }

    public void setCamera_image(String camera_image) {
        this.camera_image = camera_image;
    }

    public Boolean getSync() {
        return sync;
    }

    public void setSync(Boolean sync) {
        this.sync = sync;
    }

    public void setAverage_pace(String average_pace) {
        this.average_pace = average_pace;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getAverage_pace() {
        return average_pace;
    }

    public String getDistance() {
        return distance;
    }
}

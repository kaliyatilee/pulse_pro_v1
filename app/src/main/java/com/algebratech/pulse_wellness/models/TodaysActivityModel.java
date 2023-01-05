package com.algebratech.pulse_wellness.models;

public class TodaysActivityModel {
    String id;
    String user_id;
    String act_id;
    String time_taken;
    String distance;
    String kcals;
    String avarage_heart_rate;
    String steps;
    String avg_pace;
    String camera_file;
    String activity;
    String created_at;
    String updated_at;
    String username;
    String firstname;
    String lastname;
    String profileurl;

    public TodaysActivityModel(String id, String user_id, String act_id, String time_taken, String distance, String kcals, String avarage_heart_rate, String steps, String avg_pace, String camera_file, String activity, String created_at, String updated_at, String username, String firstname, String lastname, String profileurl) {
        this.id = id;
        this.user_id = user_id;
        this.act_id = act_id;
        this.time_taken = time_taken;
        this.distance = distance;
        this.kcals = kcals;
        this.avarage_heart_rate = avarage_heart_rate;
        this.steps = steps;
        this.avg_pace = avg_pace;
        this.camera_file = camera_file;
        this.activity = activity;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.profileurl = profileurl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAct_id() {
        return act_id;
    }

    public void setAct_id(String act_id) {
        this.act_id = act_id;
    }

    public String getTime_taken() {
        return time_taken;
    }

    public void setTime_taken(String time_taken) {
        this.time_taken = time_taken;
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

    public String getAvarage_heart_rate() {
        return avarage_heart_rate;
    }

    public void setAvarage_heart_rate(String avarage_heart_rate) {
        this.avarage_heart_rate = avarage_heart_rate;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getAvg_pace() {
        return avg_pace;
    }

    public void setAvg_pace(String avg_pace) {
        this.avg_pace = avg_pace;
    }

    public String getCamera_file() {
        return camera_file;
    }

    public void setCamera_file(String camera_file) {
        this.camera_file = camera_file;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getProfileurl() {
        return profileurl;
    }

    public void setProfileurl(String profileurl) {
        this.profileurl = profileurl;
    }
}

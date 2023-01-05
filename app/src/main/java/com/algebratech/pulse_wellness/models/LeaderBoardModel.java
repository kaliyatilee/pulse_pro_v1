package com.algebratech.pulse_wellness.models;

public class LeaderBoardModel {
    private String user_id;
    private String friend_id;
    private String steps;
    private String firstname;
    private String lastname;
    private String profileurl;
    private int count;

    public LeaderBoardModel(String user_id, String friend_id, String steps, String firstname, String lastname, String profileurl, int count) {
        this.user_id = user_id;
        this.friend_id = friend_id;
        this.steps = steps;
        this.firstname = firstname;
        this.lastname = lastname;
        this.profileurl = profileurl;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(String friend_id) {
        this.friend_id = friend_id;
    }


    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
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

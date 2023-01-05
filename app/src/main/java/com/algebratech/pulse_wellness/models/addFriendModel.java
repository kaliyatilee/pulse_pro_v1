package com.algebratech.pulse_wellness.models;

public class addFriendModel {
    private String userName;
    private int profileImage;

    public addFriendModel(String userName, int profileImage) {
        this.userName = userName;
        this.profileImage = profileImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(int profileImage) {
        this.profileImage = profileImage;
    }
}

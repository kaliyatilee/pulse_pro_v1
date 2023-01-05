package com.algebratech.pulse_wellness.models;

public class LikeModel {
    private String userName;
    private String likeTime;
    private String userImage;
    public LikeModel(String userName, String userImage, String likeTime) {
        this.userName = userName;
        this.userImage = userImage;
        this.likeTime = likeTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getLikeTime() {
        return likeTime;
    }

    public void setLikeTime(String likeTime) {
        this.likeTime = likeTime;
    }
}

package com.algebratech.pulse_wellness.models;

public class FriendsModel {

    private String name,profilepic,userid,point,timeAgo;


    public FriendsModel (String name,String profilepic, String userid,String point,String timeAgo){

        this.name = name;
        this.profilepic = profilepic;
        this.userid = userid;
        this.point = point;
        this.timeAgo = timeAgo;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDataUser(String userid) {
        this.userid = userid;
    }

    public String getDataUser() {
        return userid;
    }


    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }
}


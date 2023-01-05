package com.algebratech.pulse_wellness.models;

public class gridActivityModel {

    private String activity_name;
    private int activity_image;

    public gridActivityModel(String course_name, int imgid) {
        this.activity_name = course_name;
        this.activity_image = imgid;
    }

    public String getActivity_name() {
        return activity_name;
    }

    public void setActivity_name(String activity_name) {
        this.activity_name = activity_name;
    }

    public int getActivity_image() {
        return activity_image;
    }

    public void setActivity_image(int activity_image) {
        this.activity_image = activity_image;
    }

}

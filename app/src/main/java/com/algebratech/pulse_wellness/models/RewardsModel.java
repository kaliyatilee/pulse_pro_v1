package com.algebratech.pulse_wellness.models;

public class RewardsModel {

    private String id,title, image,description,pulse_points;


    public RewardsModel (String id,String title, String description,String pulse_points, String image){

        this.id = id;
        this.title = title;
        this.image = image;
        this.description = description;
        this.pulse_points = pulse_points;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPoints() {
        return pulse_points;
    }

    public void setRating(String pulse_points) {
        this.pulse_points = pulse_points;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}

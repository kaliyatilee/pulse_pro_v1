package com.algebratech.pulse_wellness.models;

public class RewardsModel {

    private String id;
    private String title;
    private String image;
    private String description;
    private String pulse_points;
    private String merchantName;

    public RewardsModel(String id, String title, String image, String description, String pulse_points, String merchantName) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.description = description;
        this.pulse_points = pulse_points;
        this.merchantName = merchantName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setPoints(String pulse_points) {
        this.pulse_points = pulse_points;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }
}

package com.algebratech.pulse_wellness.models;

public class ActivitiesSummaryModel {
    int activityImage;
    String activityTitle;
    String activityTime;
    String activityData;

    public ActivitiesSummaryModel(int activityImage, String activityTitle, String activityTime, String activityData) {
        this.activityImage = activityImage;
        this.activityTitle = activityTitle;
        this.activityTime = activityTime;
        this.activityData = activityData;
    }

    public int getActivityImage() {
        return activityImage;
    }

    public void setActivityImage(int activityImage) {
        this.activityImage = activityImage;
    }

    public String getActivityTitle() {
        return activityTitle;
    }

    public void setActivityTitle(String activityTitle) {
        this.activityTitle = activityTitle;
    }

    public String getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(String activityTime) {
        this.activityTime = activityTime;
    }

    public String getActivityData() {
        return activityData;
    }

    public void setActivityData(String activityData) {
        this.activityData = activityData;
    }
}

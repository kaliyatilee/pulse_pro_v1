package com.algebratech.pulse_wellness.models;

public class OutdoorResultModel {
    public OutdoorResultModel() {
    }

    String userId;
    long timeStamp;
    double steps;
    double calories;
    double distance;
    String heartRate;
    String activityId;
    String timeTaken;
    String avaragePace;

    public OutdoorResultModel(String userId, long timeStamp, double steps, double calories, double distance, String heartRate, String activityId, String timeTaken, String avaragePace) {
        this.userId = userId;
        this.timeStamp = timeStamp;
        this.steps = steps;
        this.calories = calories;
        this.distance = distance;
        this.heartRate = heartRate;
        this.activityId = activityId;
        this.timeTaken = timeTaken;
        this.avaragePace = avaragePace;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getSteps() {
        return steps;
    }

    public void setSteps(double steps) {
        this.steps = steps;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        this.timeTaken = timeTaken;
    }

    public String getAvaragePace() {
        return avaragePace;
    }

    public void setAvaragePace(String avaragePace) {
        this.avaragePace = avaragePace;
    }
}
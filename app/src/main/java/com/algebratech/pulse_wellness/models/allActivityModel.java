package com.algebratech.pulse_wellness.models;

public class allActivityModel {
    String time;
    String calories;
    String steps;
    String points;
    String activityName;
    public allActivityModel(String time, String calories, String steps, String points,String activityName) {
        this.time = time;
        this.calories = calories;
        this.steps = steps;
        this.points = points;
        this.activityName = activityName;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }
}

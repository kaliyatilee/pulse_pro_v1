package com.algebratech.pulse_wellness.models;

public class GraphModel {
    String label;
    String distance;
    String steps;
    String calories;

    public GraphModel(String label, String distance, String steps, String calories) {
        this.label = label;
        this.distance = distance;
        this.steps = steps;
        this.calories = calories;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }
}

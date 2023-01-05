package com.algebratech.pulse_wellness.models;

public class WellnessPlanModel {
    String plan_name;
    String steps;
    String daily_distance;
    String calories_burnt;
    String frequency_of_activity;
    String daily_calorie_intake;
    String daily_reminder;
    String duration_of_exercise;
    String participation_in_rigorous_school_sport;
    String recommended_calorie_deficit;

    public WellnessPlanModel(String plan_name, String steps, String daily_distance, String calories_burnt, String frequency_of_activity, String daily_calorie_intake, String daily_reminder, String duration_of_exercise, String participation_in_rigorous_school_sport, String recommended_calorie_deficit) {
        this.plan_name = plan_name;
        this.steps = steps;
        this.daily_distance = daily_distance;
        this.calories_burnt = calories_burnt;
        this.frequency_of_activity = frequency_of_activity;
        this.daily_calorie_intake = daily_calorie_intake;
        this.daily_reminder = daily_reminder;
        this.duration_of_exercise = duration_of_exercise;
        this.participation_in_rigorous_school_sport = participation_in_rigorous_school_sport;
        this.recommended_calorie_deficit = recommended_calorie_deficit;
    }

    public String getPlan_name() {
        return plan_name;
    }

    public void setPlan_name(String plan_name) {
        this.plan_name = plan_name;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getDaily_distance() {
        return daily_distance;
    }

    public void setDaily_distance(String daily_distance) {
        this.daily_distance = daily_distance;
    }

    public String getCalories_burnt() {
        return calories_burnt;
    }

    public void setCalories_burnt(String calories_burnt) {
        this.calories_burnt = calories_burnt;
    }

    public String getFrequency_of_activity() {
        return frequency_of_activity;
    }

    public void setFrequency_of_activity(String frequency_of_activity) {
        this.frequency_of_activity = frequency_of_activity;
    }

    public String getDaily_calorie_intake() {
        return daily_calorie_intake;
    }

    public void setDaily_calorie_intake(String daily_calorie_intake) {
        this.daily_calorie_intake = daily_calorie_intake;
    }

    public String getDaily_reminder() {
        return daily_reminder;
    }

    public void setDaily_reminder(String daily_reminder) {
        this.daily_reminder = daily_reminder;
    }

    public String getDuration_of_exercise() {
        return duration_of_exercise;
    }

    public void setDuration_of_exercise(String duration_of_exercise) {
        this.duration_of_exercise = duration_of_exercise;
    }

    public String getParticipation_in_rigorous_school_sport() {
        return participation_in_rigorous_school_sport;
    }

    public void setParticipation_in_rigorous_school_sport(String participation_in_rigorous_school_sport) {
        this.participation_in_rigorous_school_sport = participation_in_rigorous_school_sport;
    }

    public String getRecommended_calorie_deficit() {
        return recommended_calorie_deficit;
    }

    public void setRecommended_calorie_deficit(String recommended_calorie_deficit) {
        this.recommended_calorie_deficit = recommended_calorie_deficit;
    }
}

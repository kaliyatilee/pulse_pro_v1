package com.algebratech.pulse_wellness.models;

public class GreetingsNotificationModel {
    private String greetingText;
    private int greetingImage;
    public GreetingsNotificationModel(String greetingText, int greetingImage) {
        this.greetingText = greetingText;
        this.greetingImage = greetingImage;
    }

    public String getGreetingText() {
        return greetingText;
    }

    public void setGreetingText(String greetingText) {
        this.greetingText = greetingText;
    }

    public int getGreetingImage() {
        return greetingImage;
    }

    public void setGreetingImage(int greetingImage) {
        this.greetingImage = greetingImage;
    }
}

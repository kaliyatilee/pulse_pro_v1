package com.algebratech.pulse_wellness.models;

public class WeightRecordModel {
    public WeightRecordModel() {
    }

    long dateRecorded;
    double weight;
    int intValue;

    public WeightRecordModel(long dateRecorded, double weight, int intValue) {
        this.dateRecorded = dateRecorded;
        this.weight = weight;
        this.intValue = intValue;
    }

    public long getDateRecorded() {
        return dateRecorded;
    }

    public void setDateRecorded(long dateRecorded) {
        this.dateRecorded = dateRecorded;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }
}

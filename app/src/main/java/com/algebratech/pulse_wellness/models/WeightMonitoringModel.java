package com.algebratech.pulse_wellness.models;

public class WeightMonitoringModel {
    Long dateRecorded;
    Float weight;
    public WeightMonitoringModel(){}

    public WeightMonitoringModel(Long dateRecorded, Float weight) {
        this.dateRecorded = dateRecorded;
        this.weight = weight;
    }

    public Long getDateRecorded() {
        return dateRecorded;
    }

    public void setDateRecorded(Long dateRecorded) {
        this.dateRecorded = dateRecorded;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

}

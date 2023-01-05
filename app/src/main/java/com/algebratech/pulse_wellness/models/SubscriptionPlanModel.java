package com.algebratech.pulse_wellness.models;

public class SubscriptionPlanModel {
    String id;
    String plan_name;
    String amount;
    String description;
    String days;
    String created_at;
    String updated_at;
    String expiry_date;
    boolean selected;
    String subscriptions_id;

    public SubscriptionPlanModel(String id, String plan_name, String amount, String description, String days, String created_at, String updated_at ,String expiry_date, boolean selected) {
        this.id = id;
        this.plan_name = plan_name;
        this.amount = amount;
        this.description = description;
        this.days = days;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.expiry_date = expiry_date;
        this.selected = selected;
        this.expiry_date = expiry_date;
        this.subscriptions_id = subscriptions_id;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }

    public String getSubscriptions_id() {
        return subscriptions_id;
    }

    public void setSubscriptions_id(String subscriptions_id) {
        this.subscriptions_id = subscriptions_id;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlan_name() {
        return plan_name;
    }

    public void setPlan_name(String plan_name) {
        this.plan_name = plan_name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

}

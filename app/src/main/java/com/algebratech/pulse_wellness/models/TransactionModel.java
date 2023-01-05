package com.algebratech.pulse_wellness.models;

public class TransactionModel {

    private String id;
    private String user_id;
    private String reward_id;
    private String transaction_id;
    private String redeem_key;
    private String price;
    private String status;
    private String created_at;
    private String updated_at;
    private String partner_id;
    private String reward_name;
    private String amount;
    private String pulse_points;
    private String description;
    private String imageurl;
    private String name;

    public TransactionModel(String id, String user_id, String reward_id, String transaction_id, String redeem_key, String price, String status, String created_at, String updated_at, String partner_id, String reward_name, String amount, String pulse_points, String description, String imageurl, String name) {
        this.id = id;
        this.user_id = user_id;
        this.reward_id = reward_id;
        this.transaction_id = transaction_id;
        this.redeem_key = redeem_key;
        this.price = price;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.partner_id = partner_id;
        this.reward_name = reward_name;
        this.amount = amount;
        this.pulse_points = pulse_points;
        this.description = description;
        this.imageurl = imageurl;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getReward_id() {
        return reward_id;
    }

    public void setReward_id(String reward_id) {
        this.reward_id = reward_id;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getRedeem_key() {
        return redeem_key;
    }

    public void setRedeem_key(String redeem_key) {
        this.redeem_key = redeem_key;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getPartner_id() {
        return partner_id;
    }

    public void setPartner_id(String partner_id) {
        this.partner_id = partner_id;
    }

    public String getReward_name() {
        return reward_name;
    }

    public void setReward_name(String reward_name) {
        this.reward_name = reward_name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPulse_points() {
        return pulse_points;
    }

    public void setPulse_points(String pulse_points) {
        this.pulse_points = pulse_points;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package com.algebratech.pulse_wellness.models;

public class TransactionCollectedModel {
    private String name;
    private String coins;
    private String collectionDate;
    private String storeName;
    private int imgId;

    public TransactionCollectedModel(String name, String coins, String collectionDate, String storeName, int imgId) {
        this.name = name;
        this.coins = coins;
        this.collectionDate = collectionDate;
        this.storeName = storeName;
        this.imgId = imgId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoins() {
        return coins;
    }

    public void setCoins(String coins) {
        this.coins = coins;
    }

    public String getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(String collectionDate) {
        this.collectionDate = collectionDate;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }
}


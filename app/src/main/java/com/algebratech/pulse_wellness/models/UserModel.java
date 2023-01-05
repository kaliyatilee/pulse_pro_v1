package com.algebratech.pulse_wellness.models;

public class UserModel {
    public UserModel(String firstname, String lastname, String phoneNumber, String dateOfbirth, String weight, String height, String gender, String userId, String s, String s1, String email, String s2, String s3, String s4, int i, double v, Long timeStamp, WeightRecordModel weightRecord) {
    }

    String firstname;
    String lastname;
    String phone;
    String dob;
    String weight;
    String height;
    String gender;
    String patientId;
    String medSociety;
    String expiryDate;
    String email;
    String countryCode;
    String title;
    String profileUrl;
    long loyaltPoints;
    double bmi;
    long dateCreated;
    WeightRecordModel weightRecord;


    public UserModel(String firstname, String lastname, String phone, String dob, String weight, String height, String gender, String patientId, String medSociety, String expiryDate, String email, String countryCode, String title, String profileUrl, long loyaltPoints, double bmi, long dateCreated ,WeightRecordModel weightRecord) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.phone = phone;
        this.dob = dob;
        this.weight = weight;
        this.height = height;
        this.gender = gender;
        this.patientId = patientId;
        this.medSociety = medSociety;
        this.expiryDate = expiryDate;
        this.email = email;
        this.countryCode = countryCode;
        this.title = title;
        this.profileUrl = profileUrl;
        this.loyaltPoints = loyaltPoints;
        this.bmi = bmi;
        this.dateCreated = dateCreated;
        this.weightRecord = weightRecord;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getMedSociety() {
        return medSociety;
    }

    public void setMedSociety(String medSociety) {
        this.medSociety = medSociety;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public long getLoyaltPoints() {
        return loyaltPoints;
    }

    public void setLoyaltPoints(long loyaltPoints) {
        this.loyaltPoints = loyaltPoints;
    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public WeightRecordModel getWeightRecord() {
        return weightRecord;
    }

    public void setWeightRecord(WeightRecordModel weightRecord) {
        this.weightRecord = weightRecord;
    }
}

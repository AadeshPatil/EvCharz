package com.project.evcharz.Model;

public class UserModel {


    String id;
    String name;
    String emailId;
    String mobileNo;

    public UserModel(String id, String name, String emailId, String mobileNo) {
        this.id = id;
        this.name = name;
        this.emailId = emailId;
        this.mobileNo = mobileNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public UserModel() {
    }
}

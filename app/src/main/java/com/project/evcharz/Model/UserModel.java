package com.project.evcharz.Model;

public class UserModel {

    String name;
    String emailId;
    String mobileNo;



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

    public UserModel(String name, String emailId, String mobileNo) {
        this.name = name;
        this.emailId = emailId;
        this.mobileNo = mobileNo;
    }

    public UserModel() {
    }
}

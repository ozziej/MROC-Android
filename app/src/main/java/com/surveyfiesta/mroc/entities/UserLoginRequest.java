package com.surveyfiesta.mroc.entities;

public class UserLoginRequest {
    private String emailAddress;
    private String password;

    public UserLoginRequest() {
    }

    public UserLoginRequest(String emailAddress, String password) {
        this.emailAddress = emailAddress;
        this.password = password;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

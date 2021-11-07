package com.surveyfiesta.mroc.entities;

public class UserResponse extends GenericResponse {
    private Users user;

    public UserResponse() {
    }

    public UserResponse(ResponseCode responseCode, String responseMessage, RequestCode requestCode, Users user) {
        super(responseCode, responseMessage, requestCode);
        this.user = user;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }
}

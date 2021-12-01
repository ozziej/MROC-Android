package com.surveyfiesta.mroc.entities;

public class UserResponse extends GenericResponse {
    private Users user;
    private String token;

    public UserResponse() {
        super(ResponseCode.SUCCESSFUL, "", RequestCode.USER);
    }

    public UserResponse(ResponseCode responseCode, String responseMessage, RequestCode requestCode, Users user, String token) {
        super(responseCode, responseMessage, requestCode);
        this.user = user;
        this.token = token;
    }

    public UserResponse(Users user, String token){
        super(ResponseCode.SUCCESSFUL, "", RequestCode.USER);
        this.user = user;
        this.token = token;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

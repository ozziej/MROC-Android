package com.surveyfiesta.mroc.entities;

public class GenericResponse {
    private ResponseCode responseCode;
    private String responseMessage;
    private RequestCode requestCode;

    public enum ResponseCode {
        ERROR,
        SUCCESSFUL,
        NO_RESPONSE
    }

    public enum RequestCode {
        OTHER,
        IMAGE,
        COMPANY,
        USER,
        USER_NICKNAME,
        SURVEY,
        PRODUCT,
        JOIN_CHAT,
        LEAVE_CHAT,
        EDIT_CHAT,
        CHAT;
    }

    public enum RequestTypes {
        LEAVE_GROUP,
        NEW_GROUP,
        EDIT_GROUP,
        ADD_USER
    }

    public GenericResponse() {
    }

    public GenericResponse(ResponseCode responseCode, String responseMessage, RequestCode requestCode) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.requestCode = requestCode;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public RequestCode getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(RequestCode requestCode) {
        this.requestCode = requestCode;
    }
}
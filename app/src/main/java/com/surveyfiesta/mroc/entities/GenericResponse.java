package com.surveyfiesta.mroc.entities;

public class GenericResponse {
    private final ResponseCode responseCode;
    private final String responseMessage;
    private final RequestCode requestCode;
    private final String itemIdentifier;

    public enum ResponseCode{
        ERROR,
        SUCCESSFUL,
        NO_RESPONSE
    }

    public enum RequestCode{
        OTHER,
        IMAGE,
        COMPANY,
        USER,
        SURVEY,
        PRODUCT
    }

    public GenericResponse(ResponseCode responseCode, String responseMessage, RequestCode requestCode, String itemIdentifier) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.requestCode = requestCode;
        this.itemIdentifier = itemIdentifier;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public RequestCode getRequestCode() {
        return requestCode;
    }

    public String getItemIdentifier() {
        return itemIdentifier;
    }
}
package com.surveyfiesta.mroc.entities;

public class GroupMessage {
    Long timestamp;
    Long sequence;
    String messageBody;
    Integer userId;

    public GroupMessage(Long timestamp, Long sequence, String messageBody, Integer userId) {
        this.timestamp = timestamp;
        this.sequence = sequence;
        this.messageBody = messageBody;
        this.userId = userId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}

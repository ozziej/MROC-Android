package com.surveyfiesta.mroc.entities;

public class GroupChatItemRequest {
    String userToken;
    Integer groupId;
    String sequenceString;
    int count;

    public GroupChatItemRequest() {
    }

    public GroupChatItemRequest(String userToken, Integer groupId, String sequenceString, int count) {
        this.userToken = userToken;
        this.groupId = groupId;
        this.sequenceString = sequenceString;
        this.count = count;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getSequenceString() {
        return sequenceString;
    }

    public void setSequenceString(String sequenceString) {
        this.sequenceString = sequenceString;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

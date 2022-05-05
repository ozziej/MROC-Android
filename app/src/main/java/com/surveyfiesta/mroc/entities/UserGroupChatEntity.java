package com.surveyfiesta.mroc.entities;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class UserGroupChatEntity implements Serializable {
    private String userToken;
    private GroupChat groupChat;
    private List<GroupUsers> groupUsers;

    public UserGroupChatEntity() {
    }

    public UserGroupChatEntity(String userToken, GroupChat groupChat, List<GroupUsers> groupUsers) {
        this.userToken = userToken;
        this.groupChat = groupChat;
        this.groupUsers = groupUsers;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public GroupChat getGroupChat() {
        return groupChat;
    }

    public void setGroupChat(GroupChat groupChat) {
        this.groupChat = groupChat;
    }

    public List<GroupUsers> getGroupUsers() {
        return groupUsers;
    }

    public void setGroupUsers(List<GroupUsers> groupUsers) {
        this.groupUsers = groupUsers;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.groupChat);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserGroupChatEntity other = (UserGroupChatEntity) obj;
        return Objects.equals(this.groupChat, other.groupChat);
    }
}

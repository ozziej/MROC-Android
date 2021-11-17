package com.surveyfiesta.mroc.entities;

import java.io.Serializable;

public class UserGroupChatRequest implements Serializable {
    private GroupUsers groupUsers;
    private GroupChat groupChat;

    public UserGroupChatRequest() {
    }

    public UserGroupChatRequest(GroupUsers groupUsers, GroupChat groupChat) {
        this.groupUsers = groupUsers;
        this.groupChat = groupChat;
    }

    public GroupUsers getGroupUsers() {
        return groupUsers;
    }

    public void setGroupUsers(GroupUsers groupUsers) {
        this.groupUsers = groupUsers;
    }

    public GroupChat getGroupChat() {
        return groupChat;
    }

    public void setGroupChat(GroupChat groupChat) {
        this.groupChat = groupChat;
    }
}

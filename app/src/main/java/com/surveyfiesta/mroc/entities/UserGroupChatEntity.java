package com.surveyfiesta.mroc.entities;

import java.io.Serializable;
import java.util.List;

public class UserGroupChatEntity implements Serializable {

    private GroupChat groupChat;
    private List<GroupUsers> groupUsers;

    public UserGroupChatEntity() {
    }

    public UserGroupChatEntity(GroupChat groupChat, List<GroupUsers> groupUsers) {
        this.groupChat = groupChat;
        this.groupUsers = groupUsers;
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
}

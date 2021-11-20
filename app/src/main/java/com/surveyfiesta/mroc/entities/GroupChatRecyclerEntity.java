package com.surveyfiesta.mroc.entities;

public class GroupChatRecyclerEntity {
    private UserGroupChatEntity chatEntity;
    private boolean menuVisible;

    public GroupChatRecyclerEntity(UserGroupChatEntity chatEntity) {
        this.chatEntity = chatEntity;
        this.menuVisible = false;
    }

    public UserGroupChatEntity getChatEntity() {
        return chatEntity;
    }

    public void setChatEntity(UserGroupChatEntity chatEntity) {
        this.chatEntity = chatEntity;
    }

    public boolean isMenuVisible() {
        return menuVisible;
    }

    public void setMenuVisible(boolean menuVisible) {
        this.menuVisible = menuVisible;
    }
}

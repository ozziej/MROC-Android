package com.surveyfiesta.mroc.entities;

import java.util.Objects;

public class GroupUsers {
    Integer userId;
    String nickname;
    boolean adminUser;

    public GroupUsers() {
    }

    public GroupUsers(Integer userId, String nickname, boolean adminUser) {
        this.userId = userId;
        this.nickname = nickname;
        this.adminUser = adminUser;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isAdminUser() {
        return adminUser;
    }

    public void setAdminUser(boolean adminUser) {
        this.adminUser = adminUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupUsers that = (GroupUsers) o;
        return userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}

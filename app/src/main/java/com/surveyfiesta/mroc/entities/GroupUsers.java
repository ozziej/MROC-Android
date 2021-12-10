package com.surveyfiesta.mroc.entities;

import java.util.Objects;

public class GroupUsers {

    Users user;
    boolean adminUser;

    public GroupUsers() {
    }

    public GroupUsers(Users user, boolean adminUser) {
        this.adminUser = adminUser;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public boolean isAdminUser() {
        return adminUser;
    }

    public void setAdminUser(boolean adminUser) {
        this.adminUser = adminUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GroupUsers that = (GroupUsers) o;
        return user.equals(that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }
}

package com.surveyfiesta.mroc.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.surveyfiesta.mroc.constants.UserTypes;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Users implements Serializable {
    private Integer userId;
    private String userPass;
    private String userSalt;
    private String idNumber;
    private String title;
    private String firstName;
    private String surname;
    private String otherName;
    private String gender;
    private String emailAddress;
    private String countryCode;
    private String city;
    private String phoneNumber;
    private String cellNumber;
    private String postalAddress;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime dateOfBirth;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime firstRegistered;
    private String userType;
    private boolean paidParticipation;
    private boolean receiveMarketing;

    public Users() {
        this(0);
    }

    public Users(Integer userId) {
        LocalDateTime currentTime = LocalDateTime.now();
        this.userId = userId;
        this.userPass = "";
        this.userSalt = "";
        this.idNumber = null;
        this.title = "Mr.";
        this.firstName = "";
        this.surname = "";
        this.otherName = "";
        this.gender = "MALE";
        this.emailAddress = "";
        this.postalAddress = "None";
        this.city = "None";
        this.countryCode = "ZA";
        this.phoneNumber = "+27";
        this.cellNumber = "+27";
        this.firstRegistered = currentTime;
        this.dateOfBirth = currentTime;
        this.userType = UserTypes.NEW.toString();
        this.paidParticipation = false;
        this.receiveMarketing = false;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public String getUserSalt() {
        return userSalt;
    }

    public void setUserSalt(String userSalt) {
        this.userSalt = userSalt;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getOtherName() {
        return otherName;
    }

    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCellNumber() {
        return cellNumber;
    }

    public void setCellNumber(String cellNumber) {
        this.cellNumber = cellNumber;
    }

    public String getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(String postalAddress) {
        this.postalAddress = postalAddress;
    }

    public LocalDateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDateTime getFirstRegistered() {
        return firstRegistered;
    }

    public void setFirstRegistered(LocalDateTime firstRegistered) {
        this.firstRegistered = firstRegistered;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public boolean isPaidParticipation() {
        return paidParticipation;
    }

    public void setPaidParticipation(boolean paidParticipation) {
        this.paidParticipation = paidParticipation;
    }

    public boolean isReceiveMarketing() {
        return receiveMarketing;
    }

    public void setReceiveMarketing(boolean receiveMarketing) {
        this.receiveMarketing = receiveMarketing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Users users = (Users) o;
        return Objects.equals(userId, users.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}

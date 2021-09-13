package com.surveyfiesta.mroc.entities;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.TimeZone;

public class InstantNotification {
    private String notificationUuid;
    private LocalDateTime dateTime;
    private LocalDateTime expiryDateTime;
    private String senderName;
    private Integer senderId;
    private Integer recipientId;
    private String notificationType;
    private String notificationText;
    private boolean messageRead;

    public InstantNotification() {
    }

    public InstantNotification(String notificationUuid, LocalDateTime dateTime, LocalDateTime expiryDateTime, String senderName, Integer senderId, Integer recipientId, String notificationType, String notificationText, boolean messageRead) {
        this.notificationUuid = notificationUuid;
        this.dateTime = dateTime;
        this.expiryDateTime = expiryDateTime;
        this.senderName = senderName;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.notificationType = notificationType;
        this.notificationText = notificationText;
        this.messageRead = messageRead;
    }

    public String getNotificationUuid() {
        return notificationUuid;
    }

    public void setNotificationUuid(String notificationUuid) {
        this.notificationUuid = notificationUuid;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateTime), TimeZone.getDefault().toZoneId());
    }

    public LocalDateTime getExpiryDateTime() {
        return expiryDateTime;
    }

    public void setExpiryDateTime(LocalDateTime expiryDateTime) {
        this.expiryDateTime = expiryDateTime;
    }

    public void setExpiryDateTime(Long expiryDateTime) {
        this.expiryDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(expiryDateTime), TimeZone.getDefault().toZoneId());
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public Integer getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Integer recipientId) {
        this.recipientId = recipientId;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public boolean isMessageRead() {
        return messageRead;
    }

    public void setMessageRead(boolean messageRead) {
        this.messageRead = messageRead;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstantNotification that = (InstantNotification) o;
        return notificationUuid.equals(that.notificationUuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(notificationUuid);
    }
}

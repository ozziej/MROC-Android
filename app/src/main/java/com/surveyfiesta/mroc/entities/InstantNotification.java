package com.surveyfiesta.mroc.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.surveyfiesta.mroc.constants.NotificationTypes;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;

@JsonInclude(Include.NON_NULL)
public class InstantNotification {
    private String notificationUuid;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiryDateTime;
    private String senderName;
    private Integer senderId;
    private Integer recipientId;
    private String notificationType;
    private String notificationText;
    private boolean messageRead;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM YYYY");

    public InstantNotification() {
        this(UUID.randomUUID().toString(), LocalDateTime.now(), null, "", 0, 0, NotificationTypes.USER.name(), "", false);
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

    public String getFormattedTime() {
        return timeFormatter.format(this.dateTime);
    }

    public String getFormattedDay() {
        return dateFormatter.format(this.dateTime);
    }

    public String getFormattedDate() {
        return formatter.format(this.dateTime);
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setDateTime(String dateTimeString) {
        this.dateTime = LocalDateTime.parse(dateTimeString, formatter);
    }

    public LocalDateTime getExpiryDateTime() {
        return expiryDateTime;
    }

    public void setExpiryDateTime(LocalDateTime expiryDateTime) {
        this.expiryDateTime = expiryDateTime;
    }

    public void setExpiryDateTime(String expiryDateTimeString) {
        this.expiryDateTime = LocalDateTime.parse(expiryDateTimeString, formatter);
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

package com.surveyfiesta.mroc.entities;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.surveyfiesta.mroc.constants.NotificationTypes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

@JsonInclude(Include.NON_NULL)
public class InstantNotification {
    private String notificationUuid;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiryDateTime;
    private String senderName;
    private Integer senderId;
    private Integer recipientId;
    private String notificationType;
    private String notificationTitle;
    private String notificationText;
    @Nullable
    private String sequenceString;
    @Nullable
    private String additionalData;
    private boolean messageRead;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");

    public InstantNotification() {
        this(UUID.randomUUID().toString(), LocalDateTime.now(), null, "", 0, 0, NotificationTypes.USER.name(), "", "", null, null, false);
    }

    public InstantNotification(String notificationUuid, LocalDateTime dateTime, LocalDateTime expiryDateTime, String senderName, Integer senderId, Integer recipientId, String notificationType, String notificationTitle, String notificationText, String sequenceString, String additionalData, boolean messageRead) {
        this.notificationUuid = notificationUuid;
        this.dateTime = dateTime;
        this.expiryDateTime = expiryDateTime;
        this.senderName = senderName;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.notificationType = notificationType;
        this.notificationTitle = notificationTitle;
        this.notificationText = notificationText;
        this.sequenceString = sequenceString;
        this.additionalData = additionalData;
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

    @JsonIgnore
    public String getFormattedTime() {
        return timeFormatter.format(this.dateTime);
    }

    @JsonIgnore
    public String getFormattedDay() {
        return dateFormatter.format(this.dateTime);
    }

    @JsonIgnore
    public String getFormattedDate() {
        return formatter.format(this.dateTime);
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public LocalDateTime getExpiryDateTime() {
        return expiryDateTime;
    }

    public void setExpiryDateTime(LocalDateTime expiryDateTime) {
        this.expiryDateTime = expiryDateTime;
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

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }
    public String getSequenceString() {
        return sequenceString;
    }

    public void setSequenceString(String sequenceString) {
        this.sequenceString = sequenceString;
    }

    public String getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(String additionalData) {
        this.additionalData = additionalData;
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

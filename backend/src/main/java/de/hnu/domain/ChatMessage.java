package de.hnu.domain;

import java.time.Instant;
import jakarta.persistence.*;

@Entity
@Table(name = "chat_message")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ride_id")
    private Integer rideId;
    
    @Column(name = "sender_id")
    private Integer senderId;      // references Person.id
    private String senderName;    // denormalized
    
    @Column(name = "recipient_id")
    private Integer recipientId;   // references Person.id
    
    @Column(length = 2000)
    private String content;
    private Instant timestamp;
    private Boolean isRead;

    public ChatMessage() {
        this.timestamp = Instant.now();
        this.isRead = false;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRideId() {
        return rideId;
    }

    public void setRideId(Integer rideId) {
        this.rideId = rideId;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Integer getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Integer recipientId) {
        this.recipientId = recipientId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        this.isRead = read;
    }
}

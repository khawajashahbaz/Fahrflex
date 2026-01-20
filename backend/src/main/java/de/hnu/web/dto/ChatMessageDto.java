package de.hnu.web.dto;

import java.time.Instant;

public class ChatMessageDto {
    public Integer id;
    public Integer rideId;
    public Integer senderId;
    public String senderName;
    public String content;
    public Instant timestamp;
    public Boolean isOwn;
}

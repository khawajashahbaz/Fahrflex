package de.hnu.web;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import de.hnu.domain.ChatMessage;
import de.hnu.domain.Person;
import de.hnu.domain.Ride;
import de.hnu.repo.ChatMessageRepository;
import de.hnu.repo.PersonRepository;
import de.hnu.repo.RideRepository;
import de.hnu.web.dto.ChatMessageDto;
import de.hnu.web.dto.SendMessageDto;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:4200")
public class ChatController {

    private final ChatMessageRepository chatRepo;
    private final PersonRepository personRepo;
    private final RideRepository rideRepo;

    public ChatController(
            ChatMessageRepository chatRepo,
            PersonRepository personRepo,
            RideRepository rideRepo) {
        this.chatRepo = chatRepo;
        this.personRepo = personRepo;
        this.rideRepo = rideRepo;
    }

    @GetMapping("/ride/{rideId}")
    public List<ChatMessageDto> getMessages(
            @PathVariable Integer rideId,
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Integer userId) {
        
        List<ChatMessage> messages = chatRepo.findByRideIdOrderByTimestampAsc(rideId);
        
        return messages.stream()
                .map(msg -> toDto(msg, userId))
                .collect(Collectors.toList());
    }

    @PostMapping("/ride/{rideId}")
    public ResponseEntity<ChatMessageDto> sendMessage(
            @PathVariable Integer rideId,
            @RequestBody SendMessageDto dto,
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Integer userId) {
        
        Person sender = personRepo.findById(userId).orElse(null);
        
        // Find the ride to determine the recipient
        Ride ride = rideRepo.findById(rideId).orElse(null);
        Integer recipientId = null;
        
        if (ride != null) {
            // If sender is driver, recipient is passenger (we'd need to look up the passenger)
            // If sender is passenger, recipient is driver
            if (userId.equals(ride.getDriverPersonId())) {
                // Sender is driver, get first passenger
                // For simplicity, we'll leave recipient null for broadcast
            } else {
                // Sender is passenger, driver is recipient
                recipientId = ride.getDriverPersonId();
            }
        }
        
        ChatMessage message = new ChatMessage();
        message.setRideId(rideId);
        message.setSenderId(userId);
        message.setSenderName(sender != null ? sender.getName() : "Unknown");
        message.setRecipientId(recipientId);
        message.setContent(dto.content);
        message.setTimestamp(Instant.now());
        message.setRead(false);
        
        chatRepo.save(message);
        
        return ResponseEntity.ok(toDto(message, userId));
    }

    @PutMapping("/ride/{rideId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Integer rideId,
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Integer userId) {
        
        List<ChatMessage> unreadMessages = chatRepo.findByRideIdAndRecipientIdAndIsReadFalse(rideId, userId);
        
        for (ChatMessage msg : unreadMessages) {
            msg.setRead(true);
            chatRepo.save(msg);
        }
        
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ride/{rideId}/unread-count")
    public ResponseEntity<Integer> getUnreadCount(
            @PathVariable Integer rideId,
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Integer userId) {
        
        List<ChatMessage> unreadMessages = chatRepo.findByRideIdAndRecipientIdAndIsReadFalse(rideId, userId);
        return ResponseEntity.ok(unreadMessages.size());
    }

    private ChatMessageDto toDto(ChatMessage message, Integer currentUserId) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.id = message.getId();
        dto.rideId = message.getRideId();
        dto.senderId = message.getSenderId();
        dto.senderName = message.getSenderName();
        dto.content = message.getContent();
        dto.timestamp = message.getTimestamp();
        dto.isOwn = currentUserId != null && currentUserId.equals(message.getSenderId());
        return dto;
    }
}

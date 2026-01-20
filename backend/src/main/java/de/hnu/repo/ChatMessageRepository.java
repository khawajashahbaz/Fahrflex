package de.hnu.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.hnu.domain.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    
    List<ChatMessage> findByRideIdOrderByTimestampAsc(Integer rideId);
    
    List<ChatMessage> findByRideIdAndRecipientIdAndIsReadFalse(Integer rideId, Integer recipientId);
    
    List<ChatMessage> findBySenderIdOrRecipientIdOrderByTimestampDesc(Integer senderId, Integer recipientId);
}

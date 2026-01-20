package de.hnu.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.hnu.domain.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    
    List<Review> findByRevieweeIdOrderByCreatedAtDesc(Integer revieweeId);
    
    List<Review> findByRevieweeIdAndRevieweeTypeOrderByCreatedAtDesc(Integer revieweeId, String revieweeType);
    
    List<Review> findByRideId(Integer rideId);
    
    List<Review> findByReviewerId(Integer reviewerId);
    
    boolean existsByBookingIdAndReviewerId(Integer bookingId, Integer reviewerId);
}

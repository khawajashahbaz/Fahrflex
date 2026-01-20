package de.hnu.web;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import de.hnu.domain.Person;
import de.hnu.domain.Review;
import de.hnu.repo.PersonRepository;
import de.hnu.repo.ReviewRepository;
import de.hnu.web.dto.CreateReviewDto;
import de.hnu.web.dto.ReviewDto;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:4200")
public class ReviewController {

    private final ReviewRepository reviewRepo;
    private final PersonRepository personRepo;

    public ReviewController(ReviewRepository reviewRepo, PersonRepository personRepo) {
        this.reviewRepo = reviewRepo;
        this.personRepo = personRepo;
    }

    @GetMapping("/driver/{driverId}")
    public List<ReviewDto> getDriverReviews(@PathVariable Integer driverId) {
        List<Review> reviews = reviewRepo.findByRevieweeIdAndRevieweeTypeOrderByCreatedAtDesc(driverId, "DRIVER");
        return reviews.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/passenger/{passengerId}")
    public List<ReviewDto> getPassengerReviews(@PathVariable Integer passengerId) {
        List<Review> reviews = reviewRepo.findByRevieweeIdAndRevieweeTypeOrderByCreatedAtDesc(passengerId, "PASSENGER");
        return reviews.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/ride/{rideId}")
    public List<ReviewDto> getRideReviews(@PathVariable Integer rideId) {
        List<Review> reviews = reviewRepo.findByRideId(rideId);
        return reviews.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<ReviewDto> createReview(
            @RequestBody CreateReviewDto dto,
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Integer userId) {
        
        // Check if user already submitted a review for this booking
        if (dto.bookingId != null && reviewRepo.existsByBookingIdAndReviewerId(dto.bookingId, userId)) {
            return ResponseEntity.badRequest().build();
        }
        
        Person reviewer = personRepo.findById(userId).orElse(null);
        
        Review review = new Review();
        review.setRideId(dto.rideId);
        review.setBookingId(dto.bookingId);
        review.setReviewerId(userId);
        review.setReviewerName(reviewer != null ? reviewer.getName() : "Anonymous");
        review.setRevieweeId(dto.driverId);
        review.setRevieweeType("DRIVER");
        review.setRating(dto.rating);
        review.setComment(dto.comment);
        review.setCategories(dto.categories);
        review.setCreatedAt(Instant.now());
        
        reviewRepo.save(review);
        
        return ResponseEntity.ok(toDto(review));
    }

    @GetMapping("/driver/{driverId}/stats")
    public ResponseEntity<ReviewStatsDto> getDriverStats(@PathVariable Integer driverId) {
        List<Review> reviews = reviewRepo.findByRevieweeIdAndRevieweeTypeOrderByCreatedAtDesc(driverId, "DRIVER");
        
        ReviewStatsDto stats = new ReviewStatsDto();
        stats.totalReviews = reviews.size();
        
        if (!reviews.isEmpty()) {
            double sum = reviews.stream()
                    .mapToInt(Review::getRating)
                    .sum();
            stats.averageRating = sum / reviews.size();
        } else {
            stats.averageRating = 0.0;
        }
        
        return ResponseEntity.ok(stats);
    }

    private ReviewDto toDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.id = review.getId();
        dto.rideId = review.getRideId();
        dto.reviewerId = review.getReviewerId();
        dto.reviewerName = review.getReviewerName();
        dto.rating = review.getRating();
        dto.comment = review.getComment();
        dto.categories = review.getCategories();
        dto.createdAt = review.getCreatedAt();
        return dto;
    }

    // Inner class for stats
    public static class ReviewStatsDto {
        public int totalReviews;
        public double averageRating;
    }
}

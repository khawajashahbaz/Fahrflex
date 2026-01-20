package de.hnu.domain;

import java.time.Instant;
import jakarta.persistence.*;

@Entity
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ride_id")
    private Integer rideId;
    
    @Column(name = "booking_id")
    private Integer bookingId;

    // Who wrote the review
    @Column(name = "reviewer_id")
    private Integer reviewerId;
    private String reviewerName;

    // Who is being reviewed
    @Column(name = "reviewee_id")
    private Integer revieweeId;
    private String revieweeType;  // "DRIVER" or "PASSENGER"

    private Integer rating;  // 1-5 stars
    
    @Column(length = 1000)
    private String comment;
    
    private String categories;  // stored as comma-separated string

    private Instant createdAt;

    public Review() {
        this.createdAt = Instant.now();
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

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(Integer reviewerId) {
        this.reviewerId = reviewerId;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public Integer getRevieweeId() {
        return revieweeId;
    }

    public void setRevieweeId(Integer revieweeId) {
        this.revieweeId = revieweeId;
    }

    public String getRevieweeType() {
        return revieweeType;
    }

    public void setRevieweeType(String revieweeType) {
        this.revieweeType = revieweeType;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}

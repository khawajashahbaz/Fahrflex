package de.hnu.web.dto;

import java.time.Instant;

public class ReviewDto {
    public Integer id;
    public Integer rideId;
    public Integer reviewerId;
    public String reviewerName;
    public Integer rating;
    public String comment;
    public String categories;
    public Instant createdAt;
}

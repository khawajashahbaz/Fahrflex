package de.hnu.web.dto;

import java.time.Instant;

public class RideHistoryDto {
    public Integer rideId;
    public String departureCity;
    public String destinationCity;
    public Instant departureTime;
    public String role; // "DRIVER" or "PASSENGER"
}
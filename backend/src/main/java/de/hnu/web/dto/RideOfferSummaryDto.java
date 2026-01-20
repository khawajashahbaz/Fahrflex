package de.hnu.web.dto;

import java.time.Instant;

public class RideOfferSummaryDto {
    public Integer id;
    public String departureCity;
    public String destinationCity;
    public Instant departureTime;
    public Integer seatsAvailable;
    public Integer luggageCount;
    public Double pricePerPerson;

    public String driverName;
    public Integer driverChatinessLevel;
    public Integer driverOverallKmCovered;

    public String carMake;
    public String carModel;
}
package de.hnu.web.dto;

import de.hnu.domain.enums.PaymentMethod;

public class CreateRideRequestDto {
    public Integer rideOfferId;
    public Integer personId;
    public String pickupLocation;
    public String dropoffLocation;

    public Integer luggageCount;
    public Boolean pet;
    public Boolean kid;

    public PaymentMethod paymentMethod;
}
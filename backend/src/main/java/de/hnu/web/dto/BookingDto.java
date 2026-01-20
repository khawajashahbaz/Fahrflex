package de.hnu.web.dto;

import java.time.Instant;

public class BookingDto {
    public Integer id;
    public Integer rideId;
    public Integer rideOfferId;
    public String departureCity;
    public String destinationCity;
    public Instant departureTime;
    public String pickupLocation;
    public String dropoffLocation;
    public Double pricePerPerson;
    public Double totalPrice;
    public String status;
    public String paymentMethod;
    public String paymentStatus;
    public String driverName;
    public Integer driverId;
    public String driverPhone;
    public String carMake;
    public String carModel;
    public String carPlate;
    public Integer luggageCount;
    public Boolean pet;
    public Boolean kid;
}

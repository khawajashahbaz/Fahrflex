package de.hnu.domain;

import java.time.Instant;
import jakarta.persistence.*;

@Entity
@Table(name = "ride")
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ride_offer_id")
    private Integer rideOfferId; // references RideOffer.id

    private String departureCity;
    private String destinationCity;
    private Instant departureTime;

    @Column(name = "driver_person_id")
    private Integer driverPersonId; // references Person.id

    // optional cached remaining capacity (source of truth stays RideOffer)
    private Integer seatsRemaining;
    private Integer luggageRemaining;
    private String status; // PENDING, IN_PROGRESS, COMPLETED, CANCELLED

    public Ride() {}

    // getters/setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRideOfferId() {
        return rideOfferId;
    }

    public void setRideOfferId(Integer rideOfferId) {
        this.rideOfferId = rideOfferId;
    }

    public String getDepartureCity() {
        return departureCity;
    }

    public void setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public Instant getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Instant departureTime) {
        this.departureTime = departureTime;
    }

    public Integer getDriverPersonId() {
        return driverPersonId;
    }

    public void setDriverPersonId(Integer driverPersonId) {
        this.driverPersonId = driverPersonId;
    }

    public Integer getSeatsRemaining() {
        return seatsRemaining;
    }

    public void setSeatsRemaining(Integer seatsRemaining) {
        this.seatsRemaining = seatsRemaining;
    }

    public Integer getLuggageRemaining() {
        return luggageRemaining;
    }

    public void setLuggageRemaining(Integer luggageRemaining) {
        this.luggageRemaining = luggageRemaining;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
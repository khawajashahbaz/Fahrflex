package de.hnu.domain;

import java.time.Instant;
import jakarta.persistence.*;
import de.hnu.domain.enums.PaymentMethod;
import de.hnu.domain.enums.RideRequestStatus;

@Entity
@Table(name = "riderequest")
public class RideRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ride_offer_id")
    private Integer rideOfferId; // references RideOffer.id

    // passenger identity
    @Column(name = "person_id")
    private Integer personId; // references Person.id

    // trip details (passenger-specific)
    private String pickupLocation;
    private String dropoffLocation;
    private Instant timestamp;
    private Integer luggageCount;
    private Boolean pet;
    private Boolean kid;
    
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    
    @Enumerated(EnumType.STRING)
    private RideRequestStatus status;

    // filled once accepted
    @Column(name = "ride_id")
    private Integer rideId; // references Ride.id
    
    @Column(name = "passenger_id")
    private Integer passengerId; // references Passenger.id

    public RideRequest() {}

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

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDropoffLocation() {
        return dropoffLocation;
    }

    public void setDropoffLocation(String dropoffLocation) {
        this.dropoffLocation = dropoffLocation;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getLuggageCount() {
        return luggageCount;
    }

    public void setLuggageCount(Integer luggageCount) {
        this.luggageCount = luggageCount;
    }

    public Boolean getPet() {
        return pet;
    }

    public void setPet(Boolean pet) {
        this.pet = pet;
    }

    public Boolean getKid() {
        return kid;
    }

    public void setKid(Boolean kid) {
        this.kid = kid;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public RideRequestStatus getStatus() {
        return status;
    }

    public void setStatus(RideRequestStatus status) {
        this.status = status;
    }

    public Integer getRideId() {
        return rideId;
    }

    public void setRideId(Integer rideId) {
        this.rideId = rideId;
    }

    public Integer getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Integer passengerId) {
        this.passengerId = passengerId;
    }
}
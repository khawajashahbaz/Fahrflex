package de.hnu.domain;

import jakarta.persistence.*;
import de.hnu.domain.enums.PaymentMethod;

@Entity
@Table(name = "passenger")
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ride_id")
    private Integer rideId; // references Ride.id
    
    @Column(name = "person_id")
    private Integer personId; // references Person.id

    private Integer luggageCount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    
    private Boolean paymentOutstanding;

    private String pickupLocation;
    private String dropoffLocation;

    private Boolean pet;
    private Boolean kid;

    // derived: 1 (+1 pet) (+1 kid)
    private Integer seatsConsumed;

    public Passenger() {}

    // getters/setters
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

    public Integer getPersonId() {
        return personId;
    }

    public void setPersonId(Integer personId) {
        this.personId = personId;
    }

    public Integer getLuggageCount() {
        return luggageCount;
    }

    public void setLuggageCount(Integer luggageCount) {
        this.luggageCount = luggageCount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Boolean getPaymentOutstanding() {
        return paymentOutstanding;
    }

    public void setPaymentOutstanding(Boolean paymentOutstanding) {
        this.paymentOutstanding = paymentOutstanding;
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

    public Integer getSeatsConsumed() {
        return seatsConsumed;
    }

    public void setSeatsConsumed(Integer seatsConsumed) {
        this.seatsConsumed = seatsConsumed;
    }
}
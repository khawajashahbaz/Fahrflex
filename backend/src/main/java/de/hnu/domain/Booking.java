package de.hnu.domain;

import java.time.Instant;
import jakarta.persistence.*;
import de.hnu.domain.enums.BookingStatus;
import de.hnu.domain.enums.PaymentMethod;
import de.hnu.domain.enums.PaymentStatus;

@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ride_id")
    private Integer rideId;
    
    @Column(name = "ride_offer_id")
    private Integer rideOfferId;
    
    @Column(name = "passenger_id")
    private Integer passengerId;  // references Person.id

    // Trip details
    private String departureCity;
    private String destinationCity;
    private Instant departureTime;
    private String pickupLocation;
    private String dropoffLocation;

    // Passenger options
    private Integer luggageCount;
    private Boolean pet;
    private Boolean kid;

    // Pricing
    @Column(columnDefinition = "DOUBLE")
    private Double pricePerPerson;
    @Column(columnDefinition = "DOUBLE")
    private Double totalPrice;

    // Status
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
    
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    // Timestamps
    private Instant createdAt;
    private Instant updatedAt;

    // Driver info (denormalized for convenience)
    @Column(name = "driver_id")
    private Integer driverId;
    private String driverName;
    private String driverPhone;

    // Car info (denormalized)
    private String carMake;
    private String carModel;
    private String carPlate;

    public Booking() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
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

    public Integer getRideOfferId() {
        return rideOfferId;
    }

    public void setRideOfferId(Integer rideOfferId) {
        this.rideOfferId = rideOfferId;
    }

    public Integer getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Integer passengerId) {
        this.passengerId = passengerId;
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

    public Double getPricePerPerson() {
        return pricePerPerson;
    }

    public void setPricePerPerson(Double pricePerPerson) {
        this.pricePerPerson = pricePerPerson;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getDriverId() {
        return driverId;
    }

    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getCarMake() {
        return carMake;
    }

    public void setCarMake(String carMake) {
        this.carMake = carMake;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarPlate() {
        return carPlate;
    }

    public void setCarPlate(String carPlate) {
        this.carPlate = carPlate;
    }
}

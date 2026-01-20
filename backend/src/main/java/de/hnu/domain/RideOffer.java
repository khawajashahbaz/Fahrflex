package de.hnu.domain;

import java.time.Instant;
import jakarta.persistence.*;

@Entity
@Table(name = "rideoffer")
public class RideOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String departureCity;
    private String destinationCity;
    private Instant departureTime;

    private Integer seatsAvailable;
    
    @Column(columnDefinition = "DOUBLE")
    private Double pricePerPerson;

    private Integer luggageCount;
    
    @Column(name = "driver_person_id")
    private Integer driverPersonId;    // references Person.id
    
    @Column(name = "car_id")
    private Integer carId;             // references Car.id
    
    // Preferences
    private Boolean smokingAllowed;
    private Boolean petsAllowed;
    private Boolean musicAllowed;
    private Integer chatLevel;
    
    @Column(length = 500)
    private String additionalNotes;
    
    // Time flexibility
    private Boolean flexibleTime;
    private Integer flexibilityMinutes;
    
    // Route options - stored as comma-separated string
    @Column(length = 500)
    private String stops;
    
    // Payment options - stored as comma-separated string
    private String acceptedPaymentMethods;

    public RideOffer() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getSeatsAvailable() {
        return seatsAvailable;
    }

    public void setSeatsAvailable(Integer seatsAvailable) {
        this.seatsAvailable = seatsAvailable;
    }

    public Double getPricePerPerson() {
        return pricePerPerson;
    }

    public void setPricePerPerson(Double pricePerPerson) {
        this.pricePerPerson = pricePerPerson;
    }

    public Integer getLuggageCount() {
        return luggageCount;
    }

    public void setLuggageCount(Integer luggageCount) {
        this.luggageCount = luggageCount;
    }

    public Integer getDriverPersonId() {
        return driverPersonId;
    }

    public void setDriverPersonId(Integer driverPersonId) {
        this.driverPersonId = driverPersonId;
    }

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }

    public Boolean getSmokingAllowed() {
        return smokingAllowed;
    }

    public void setSmokingAllowed(Boolean smokingAllowed) {
        this.smokingAllowed = smokingAllowed;
    }

    public Boolean getPetsAllowed() {
        return petsAllowed;
    }

    public void setPetsAllowed(Boolean petsAllowed) {
        this.petsAllowed = petsAllowed;
    }

    public Boolean getMusicAllowed() {
        return musicAllowed;
    }

    public void setMusicAllowed(Boolean musicAllowed) {
        this.musicAllowed = musicAllowed;
    }

    public Integer getChatLevel() {
        return chatLevel;
    }

    public void setChatLevel(Integer chatLevel) {
        this.chatLevel = chatLevel;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }

    public Boolean getFlexibleTime() {
        return flexibleTime;
    }

    public void setFlexibleTime(Boolean flexibleTime) {
        this.flexibleTime = flexibleTime;
    }

    public Integer getFlexibilityMinutes() {
        return flexibilityMinutes;
    }

    public void setFlexibilityMinutes(Integer flexibilityMinutes) {
        this.flexibilityMinutes = flexibilityMinutes;
    }

    public String getStops() {
        return stops;
    }

    public void setStops(String stops) {
        this.stops = stops;
    }

    public String getAcceptedPaymentMethods() {
        return acceptedPaymentMethods;
    }

    public void setAcceptedPaymentMethods(String acceptedPaymentMethods) {
        this.acceptedPaymentMethods = acceptedPaymentMethods;
    }
}
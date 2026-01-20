package de.hnu.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "car")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String make;
    private String model;
    private Integer availableSeats;
    private Integer luggageSpace;
    private Boolean smokingAllowed;
    private Boolean petsAllowed;
    private String plate;
    private Integer buildYear;

    @Column(name = "insurance_id")
    private Integer insuranceId; // references Insurance.id

    public Car() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public Integer getLuggageSpace() {
        return luggageSpace;
    }

    public void setLuggageSpace(Integer luggageSpace) {
        this.luggageSpace = luggageSpace;
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

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public Integer getBuildYear() {
        return buildYear;
    }

    public void setBuildYear(Integer buildYear) {
        this.buildYear = buildYear;
    }

    public Integer getInsuranceId() {
        return insuranceId;
    }

    public void setInsuranceId(Integer insuranceId) {
        this.insuranceId = insuranceId;
    }
}
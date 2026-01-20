package de.hnu.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String gender;
    private Integer age;

    private String profilePicture;
    private String idPicture;
    private String licensePicture;

    private String homeCity;
    
    @Column(length = 1000)
    private String bio;
    
    private String phoneNumber;
    private String email;

    private String street;
    private String postalCode;

    private String preferredTransmission;
    private String preferredCarType;
    private String preferredMusic;
    
    @Column(name = "car_id")
    private Integer carId; // references Car.id
    
    private String languages;

    private Integer chatinessLevel;
    private Integer overallKmCovered;

    public Person() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getIdPicture() {
        return idPicture;
    }

    public void setIdPicture(String idPicture) {
        this.idPicture = idPicture;
    }

    public String getLicensePicture() {
        return licensePicture;
    }

    public void setLicensePicture(String licensePicture) {
        this.licensePicture = licensePicture;
    }

    public String getHomeCity() {
        return homeCity;
    }

    public void setHomeCity(String homeCity) {
        this.homeCity = homeCity;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPreferredTransmission() {
        return preferredTransmission;
    }

    public void setPreferredTransmission(String preferredTransmission) {
        this.preferredTransmission = preferredTransmission;
    }

    public String getPreferredCarType() {
        return preferredCarType;
    }

    public void setPreferredCarType(String preferredCarType) {
        this.preferredCarType = preferredCarType;
    }

    public String getPreferredMusic() {
        return preferredMusic;
    }

    public void setPreferredMusic(String preferredMusic) {
        this.preferredMusic = preferredMusic;
    }

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public Integer getChatinessLevel() {
        return chatinessLevel;
    }

    public void setChatinessLevel(Integer chatinessLevel) {
        this.chatinessLevel = chatinessLevel;
    }

    public Integer getOverallKmCovered() {
        return overallKmCovered;
    }

    public void setOverallKmCovered(Integer overallKmCovered) {
        this.overallKmCovered = overallKmCovered;
    }
}
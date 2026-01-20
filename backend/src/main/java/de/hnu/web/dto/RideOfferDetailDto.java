package de.hnu.web.dto;

import de.hnu.domain.Car;
import de.hnu.domain.Insurance;
import de.hnu.domain.Person;
import de.hnu.domain.RideOffer;

public class RideOfferDetailDto {
    public RideOffer offer;
    public Person driver;
    public Car car;
    public Insurance insurance;
}
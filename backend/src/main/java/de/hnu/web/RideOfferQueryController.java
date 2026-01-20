package de.hnu.web;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import de.hnu.domain.Car;
import de.hnu.domain.Insurance;
import de.hnu.domain.Person;
import de.hnu.domain.RideOffer;
import de.hnu.repo.CarRepository;
import de.hnu.repo.InsuranceRepository;
import de.hnu.repo.PersonRepository;
import de.hnu.repo.RideOfferRepository;
import de.hnu.web.dto.RideOfferDetailDto;
import de.hnu.web.dto.RideOfferSummaryDto;

@RestController
@RequestMapping("/api/rideoffers")
@CrossOrigin(origins = "http://localhost:4200")
public class RideOfferQueryController {

    private final RideOfferRepository rideOfferRepo;
    private final PersonRepository personRepo;
    private final CarRepository carRepo;
    private final InsuranceRepository insuranceRepo;

    public RideOfferQueryController(
            RideOfferRepository rideOfferRepo,
            PersonRepository personRepo,
            CarRepository carRepo,
            InsuranceRepository insuranceRepo
    ) {
        this.rideOfferRepo = rideOfferRepo;
        this.personRepo = personRepo;
        this.carRepo = carRepo;
        this.insuranceRepo = insuranceRepo;
    }

    @GetMapping("/search")
    public List<RideOfferSummaryDto> search(
            @RequestParam String departureCity,
            @RequestParam String destinationCity,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<RideOffer> offers;

        if (date == null) {
            offers = rideOfferRepo.findByDepartureCityIgnoreCaseAndDestinationCityIgnoreCase(departureCity, destinationCity);
        } else {
            Instant from = date.atStartOfDay().toInstant(ZoneOffset.UTC);
            Instant to = date.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
            offers = rideOfferRepo.findByDepartureCityIgnoreCaseAndDestinationCityIgnoreCaseAndDepartureTimeBetween(
                    departureCity, destinationCity, from, to
            );
        }

        // Build a simple enriched summary (driver + car basics)
        return offers.stream().map(o -> {
            RideOfferSummaryDto dto = new RideOfferSummaryDto();
            dto.id = o.getId();
            dto.departureCity = o.getDepartureCity();
            dto.destinationCity = o.getDestinationCity();
            dto.departureTime = o.getDepartureTime();
            dto.seatsAvailable = o.getSeatsAvailable();
            dto.luggageCount = o.getLuggageCount();
            dto.pricePerPerson = o.getPricePerPerson();

            if (o.getDriverPersonId() != null) {
                Person driver = personRepo.findById(o.getDriverPersonId()).orElse(null);
                if (driver != null) {
                    dto.driverName = driver.getName();
                    dto.driverChatinessLevel = driver.getChatinessLevel();
                    dto.driverOverallKmCovered = driver.getOverallKmCovered();

                    if (driver.getCarId() != null) {
                        Car car = carRepo.findById(driver.getCarId()).orElse(null);
                        if (car != null) {
                            dto.carMake = car.getMake();
                            dto.carModel = car.getModel();
                        }
                    }
                }
            }
            return dto;
        }).toList();
    }

    @GetMapping("/{id}")
    public RideOfferDetailDto getDetail(@PathVariable Integer id) {
        RideOffer offer = rideOfferRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("RideOffer not found: " + id));

        RideOfferDetailDto dto = new RideOfferDetailDto();
        dto.offer = offer;

        if (offer.getDriverPersonId() != null) {
            dto.driver = personRepo.findById(offer.getDriverPersonId()).orElse(null);

            if (dto.driver != null && dto.driver.getCarId() != null) {
                dto.car = carRepo.findById(dto.driver.getCarId()).orElse(null);

                if (dto.car != null && dto.car.getInsuranceId() != null) {
                    dto.insurance = insuranceRepo.findById(dto.car.getInsuranceId()).orElse(null);
                }
            }
        }
        return dto;
    }
}
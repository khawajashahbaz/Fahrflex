package de.hnu.service;

import java.time.Instant;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.hnu.domain.Passenger;
import de.hnu.domain.Ride;
import de.hnu.domain.RideOffer;
import de.hnu.domain.RideRequest;
import de.hnu.domain.enums.RideRequestStatus;
import de.hnu.repo.PassengerRepository;
import de.hnu.repo.RideOfferRepository;
import de.hnu.repo.RideRepository;
import de.hnu.repo.RideRequestRepository;
import de.hnu.web.dto.CreateRideRequestDto;

@Service
public class RideFlowService {

    private final RideRequestRepository rideRequestRepo;
    private final RideRepository rideRepo;
    private final PassengerRepository passengerRepo;
    private final RideOfferRepository rideOfferRepo;
    private final TaskScheduler scheduler;

    public RideFlowService(
            RideRequestRepository rideRequestRepo,
            RideRepository rideRepo,
            PassengerRepository passengerRepo,
            RideOfferRepository rideOfferRepo,
            TaskScheduler scheduler
    ) {
        this.rideRequestRepo = rideRequestRepo;
        this.rideRepo = rideRepo;
        this.passengerRepo = passengerRepo;
        this.rideOfferRepo = rideOfferRepo;
        this.scheduler = scheduler;
    }

    public RideRequest createRideRequest(CreateRideRequestDto dto) {
        RideRequest rr = new RideRequest();
        rr.setRideOfferId(dto.rideOfferId);
        rr.setPersonId(dto.personId);
        rr.setPickupLocation(dto.pickupLocation);
        rr.setDropoffLocation(dto.dropoffLocation);
        rr.setLuggageCount(dto.luggageCount != null ? dto.luggageCount : 0);
        rr.setPet(dto.pet != null ? dto.pet : false);
        rr.setKid(dto.kid != null ? dto.kid : false);
        rr.setPaymentMethod(dto.paymentMethod);
        rr.setTimestamp(Instant.now());
        rr.setStatus(RideRequestStatus.PENDING);

        rr = rideRequestRepo.save(rr);

        // Mock driver acceptance after short delay of a few seconds
        final Integer requestId = rr.getId();
        scheduler.schedule(() -> acceptRideRequest(requestId), Instant.now().plusSeconds(7));

        return rr;
    }

    @Transactional
    public RideRequest acceptRideRequest(Integer rideRequestId) {
        RideRequest rr = rideRequestRepo.findById(rideRequestId)
                .orElseThrow(() -> new IllegalArgumentException("RideRequest not found: " + rideRequestId));

        if (rr.getStatus() != RideRequestStatus.PENDING) {
            return rr; // already processed
        }

        int seatsConsumed = 1 + (Boolean.TRUE.equals(rr.getPet()) ? 1 : 0) + (Boolean.TRUE.equals(rr.getKid()) ? 1 : 0);
        int luggageRequested = rr.getLuggageCount() != null ? rr.getLuggageCount() : 0;

        // Get the RideOffer and check capacity
        RideOffer offer = rideOfferRepo.findById(rr.getRideOfferId()).orElse(null);
        
        if (offer == null || offer.getSeatsAvailable() < seatsConsumed || offer.getLuggageCount() < luggageRequested) {
            rr.setStatus(RideRequestStatus.REJECTED);
            return rideRequestRepo.save(rr);
        }

        // Update capacity
        offer.setSeatsAvailable(offer.getSeatsAvailable() - seatsConsumed);
        offer.setLuggageCount(offer.getLuggageCount() - luggageRequested);
        final RideOffer savedOffer = rideOfferRepo.save(offer);

        // Ensure Ride exists for this offer
        Ride ride = rideRepo.findByRideOfferId(savedOffer.getId()).orElseGet(() -> {
            Ride r = new Ride();
            r.setRideOfferId(savedOffer.getId());
            r.setDepartureCity(savedOffer.getDepartureCity());
            r.setDestinationCity(savedOffer.getDestinationCity());
            r.setDepartureTime(savedOffer.getDepartureTime());
            r.setDriverPersonId(savedOffer.getDriverPersonId());
            return rideRepo.save(r);
        });

        // Cache remaining (optional)
        ride.setSeatsRemaining(savedOffer.getSeatsAvailable());
        ride.setLuggageRemaining(savedOffer.getLuggageCount());
        ride = rideRepo.save(ride);

        // Create Passenger
        Passenger p = new Passenger();
        p.setRideId(ride.getId());
        p.setPersonId(rr.getPersonId());
        p.setLuggageCount(luggageRequested);
        p.setPaymentMethod(rr.getPaymentMethod());
        p.setPaymentOutstanding(true);
        p.setPickupLocation(rr.getPickupLocation());
        p.setDropoffLocation(rr.getDropoffLocation());
        p.setPet(rr.getPet());
        p.setKid(rr.getKid());
        p.setSeatsConsumed(seatsConsumed);

        p = passengerRepo.save(p);

        rr.setRideId(ride.getId());
        rr.setPassengerId(p.getId());
        rr.setStatus(RideRequestStatus.ACCEPTED);
        return rideRequestRepo.save(rr);
    }
}
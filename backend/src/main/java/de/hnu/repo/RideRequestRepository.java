package de.hnu.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.hnu.domain.RideRequest;
import de.hnu.domain.enums.RideRequestStatus;

public interface RideRequestRepository extends JpaRepository<RideRequest, Integer> {
    List<RideRequest> findByRideOfferId(Integer rideOfferId);
    
    List<RideRequest> findByPersonId(Integer personId);
    
    List<RideRequest> findByRideOfferIdAndStatus(Integer rideOfferId, RideRequestStatus status);
    
    List<RideRequest> findByRideId(Integer rideId);
}
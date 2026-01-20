package de.hnu.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import de.hnu.domain.Ride;

public interface RideRepository extends JpaRepository<Ride, Integer> {
    Optional<Ride> findByRideOfferId(Integer rideOfferId);
    List<Ride> findByDriverPersonId(Integer driverPersonId);
}

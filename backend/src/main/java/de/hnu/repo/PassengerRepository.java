package de.hnu.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.hnu.domain.Passenger;

public interface PassengerRepository extends JpaRepository<Passenger, Integer> {
    List<Passenger> findByRideId(Integer rideId);
    List<Passenger> findByPersonId(Integer personId);
}
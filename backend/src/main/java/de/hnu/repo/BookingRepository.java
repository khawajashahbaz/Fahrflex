package de.hnu.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.hnu.domain.Booking;
import de.hnu.domain.enums.BookingStatus;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    
    List<Booking> findByPassengerId(Integer passengerId);
    
    List<Booking> findByPassengerIdAndStatus(Integer passengerId, BookingStatus status);
    
    List<Booking> findByPassengerIdAndStatusIn(Integer passengerId, List<BookingStatus> statuses);
    
    List<Booking> findByDriverId(Integer driverId);
    
    List<Booking> findByRideId(Integer rideId);
    
    List<Booking> findByRideOfferId(Integer rideOfferId);
}

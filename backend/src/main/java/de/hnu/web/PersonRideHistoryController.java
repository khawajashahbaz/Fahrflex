package de.hnu.web;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.*;

import de.hnu.domain.Passenger;
import de.hnu.domain.Ride;
import de.hnu.repo.PassengerRepository;
import de.hnu.repo.RideRepository;
import de.hnu.web.dto.RideHistoryDto;

@RestController
@RequestMapping("/api/persons")
@CrossOrigin(origins = "http://localhost:4200")
public class PersonRideHistoryController {

    private final RideRepository rideRepo;
    private final PassengerRepository passengerRepo;

    public PersonRideHistoryController(RideRepository rideRepo, PassengerRepository passengerRepo) {
        this.rideRepo = rideRepo;
        this.passengerRepo = passengerRepo;
    }

    @GetMapping("/{personId}/rides")
    public List<RideHistoryDto> getRideHistory(@PathVariable Integer personId) {
        // rides where person is driver
        List<Ride> asDriver = rideRepo.findByDriverPersonId(personId);

        // rides where person is passenger
        List<Passenger> passengerEntries = passengerRepo.findByPersonId(personId);
        Set<Integer> passengerRideIds = passengerEntries.stream()
                .map(Passenger::getRideId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<Ride> asPassenger = passengerRideIds.isEmpty()
                ? List.of()
                : rideRepo.findAllById(passengerRideIds);

        Map<Integer, RideHistoryDto> merged = new HashMap<>();

        for (Ride r : asPassenger) {
            merged.put(r.getId(), toDto(r, "PASSENGER"));
        }
        for (Ride r : asDriver) {
            // If someone is both passenger+driver (edge case), driver wins
            merged.put(r.getId(), toDto(r, "DRIVER"));
        }

        return merged.values().stream()
                .sorted((a, b) -> {
                    if (a.departureTime == null && b.departureTime == null) return 0;
                    if (a.departureTime == null) return 1;
                    if (b.departureTime == null) return -1;
                    return b.departureTime.compareTo(a.departureTime); // newest first
                })
                .toList();
    }

    private RideHistoryDto toDto(Ride r, String role) {
        RideHistoryDto dto = new RideHistoryDto();
        dto.rideId = r.getId();
        dto.departureCity = r.getDepartureCity();
        dto.destinationCity = r.getDestinationCity();
        dto.departureTime = r.getDepartureTime();
        dto.role = role;
        return dto;
    }
}
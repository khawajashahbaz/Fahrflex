package de.hnu.web;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;

import de.hnu.domain.Passenger;
import de.hnu.domain.Ride;
import de.hnu.repo.PassengerRepository;
import de.hnu.repo.RideRepository;
import de.hnu.domain.Person;
import de.hnu.repo.PersonRepository;
import de.hnu.web.dto.PersonRefDto;

@RestController
@RequestMapping("/api/rides")
@CrossOrigin(origins = "http://localhost:4200")
public class RideController {

    private final RideRepository rideRepo;
    private final PassengerRepository passengerRepo;

    private final PersonRepository personRepo;

    public RideController(RideRepository rideRepo, PassengerRepository passengerRepo, PersonRepository personRepo) {
        this.rideRepo = rideRepo;
        this.passengerRepo = passengerRepo;
        this.personRepo = personRepo;
    }

    @GetMapping("/{id}")
    public Ride getRide(@PathVariable Integer id) {
        return rideRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ride not found: " + id));
    }

    @GetMapping("/{id}/passengers")
    public List<Passenger> getPassengers(@PathVariable Integer id) {
        return passengerRepo.findByRideId(id);
    }

    @GetMapping("/{id}/participants")
    public List<PersonRefDto> getParticipants(@PathVariable Integer id) {
        Ride ride = rideRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ride not found: " + id));

        List<PersonRefDto> out = new ArrayList<>();

        // driver
        if (ride.getDriverPersonId() != null) {
            Person driver = personRepo.findById(ride.getDriverPersonId()).orElse(null);
            PersonRefDto d = new PersonRefDto();
            d.personId = ride.getDriverPersonId();
            d.name = driver != null ? driver.getName() : String.valueOf(ride.getDriverPersonId());
            d.role = "DRIVER";
            out.add(d);
        }

        // passengers
        for (var p : passengerRepo.findByRideId(id)) {
            if (p.getPersonId() == null)
                continue;
            Person person = personRepo.findById(p.getPersonId()).orElse(null);

            PersonRefDto dto = new PersonRefDto();
            dto.personId = p.getPersonId();
            dto.name = person != null ? person.getName() : String.valueOf(p.getPersonId());
            dto.role = "PASSENGER";
            out.add(dto);
        }

        // de-duplicate by personId (in case of inconsistencies)
        Map<Integer, PersonRefDto> uniq = new LinkedHashMap<>();
        for (PersonRefDto pr : out)
            uniq.put(pr.personId, pr);

        return new ArrayList<>(uniq.values());
    }
}
package de.hnu.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import de.hnu.domain.RideOffer;
import de.hnu.repo.RideOfferRepository;

@RestController
@RequestMapping("/api/rideoffers")
@CrossOrigin(origins = "http://localhost:4200")
public class RideOfferController {

    private final RideOfferRepository repo;

    public RideOfferController(RideOfferRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public RideOffer create(@RequestBody RideOffer offer) {
        // minimal validation
        if (offer.getDepartureCity() == null || offer.getDestinationCity() == null || offer.getDepartureTime() == null) {
            throw new IllegalArgumentException("departureCity, destinationCity, departureTime are required");
        }
        return repo.save(offer);
    }

    @GetMapping("/driver/{driverId}")
    public List<RideOffer> getByDriver(@PathVariable Integer driverId) {
        return repo.findByDriverPersonId(driverId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<RideOffer> update(@PathVariable Integer id, @RequestBody RideOffer offer) {
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        offer.setId(id);
        return ResponseEntity.ok(repo.save(offer));
    }
}
package de.hnu.web;

import org.springframework.web.bind.annotation.*;

import de.hnu.domain.RideRequest;
import de.hnu.repo.RideRequestRepository;
import de.hnu.service.RideFlowService;
import de.hnu.web.dto.CreateRideRequestDto;

@RestController
@RequestMapping("/api/riderequests")
@CrossOrigin(origins = "http://localhost:4200")
public class RideRequestController {

    private final RideFlowService flowService;
    private final RideRequestRepository rideRequestRepo;

    public RideRequestController(RideFlowService flowService, RideRequestRepository rideRequestRepo) {
        this.flowService = flowService;
        this.rideRequestRepo = rideRequestRepo;
    }

    @PostMapping
    public RideRequest create(@RequestBody CreateRideRequestDto dto) {
        return flowService.createRideRequest(dto);
    }

    @GetMapping("/{id}")
    public RideRequest get(@PathVariable Integer id) {
        return rideRequestRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("RideRequest not found: " + id));
    }
}
package de.hnu.web;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import de.hnu.domain.Car;
import de.hnu.domain.Person;
import de.hnu.domain.Review;
import de.hnu.domain.Ride;
import de.hnu.repo.CarRepository;
import de.hnu.repo.PersonRepository;
import de.hnu.repo.ReviewRepository;
import de.hnu.repo.RideRepository;
import de.hnu.web.dto.DriverProfileDto;
import de.hnu.web.dto.ReviewDto;

@RestController
@RequestMapping("/api/drivers")
@CrossOrigin(origins = "http://localhost:4200")
public class DriverProfileController {

    private final PersonRepository personRepo;
    private final CarRepository carRepo;
    private final ReviewRepository reviewRepo;
    private final RideRepository rideRepo;

    public DriverProfileController(
            PersonRepository personRepo,
            CarRepository carRepo,
            ReviewRepository reviewRepo,
            RideRepository rideRepo) {
        this.personRepo = personRepo;
        this.carRepo = carRepo;
        this.reviewRepo = reviewRepo;
        this.rideRepo = rideRepo;
    }

    @GetMapping("/{driverId}")
    public ResponseEntity<DriverProfileDto> getDriverProfile(@PathVariable Integer driverId) {
        Person driver = personRepo.findById(driverId).orElse(null);
        if (driver == null) {
            return ResponseEntity.notFound().build();
        }
        
        Car car = null;
        if (driver.getCarId() != null) {
            car = carRepo.findById(driver.getCarId()).orElse(null);
        }
        
        // Get reviews
        List<Review> reviews = reviewRepo.findByRevieweeIdAndRevieweeTypeOrderByCreatedAtDesc(driverId, "DRIVER");
        
        // Count completed rides
        List<Ride> rides = rideRepo.findByDriverPersonId(driverId);
        int completedRides = (int) rides.stream()
                .filter(r -> "COMPLETED".equals(r.getStatus()))
                .count();
        
        // Calculate average rating
        double avgRating = 0.0;
        if (!reviews.isEmpty()) {
            avgRating = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);
        }
        
        DriverProfileDto dto = new DriverProfileDto();
        dto.id = driver.getId();
        dto.name = driver.getName();
        dto.profilePicture = driver.getProfilePicture();
        dto.bio = driver.getBio();
        dto.homeCity = driver.getHomeCity();
        dto.age = driver.getAge();
        dto.languages = driver.getLanguages();
        dto.chatinessLevel = driver.getChatinessLevel();
        dto.overallKmCovered = driver.getOverallKmCovered();
        dto.totalRides = completedRides > 0 ? completedRides : rides.size();
        dto.averageRating = Math.round(avgRating * 10.0) / 10.0;
        dto.totalReviews = reviews.size();
        
        if (car != null) {
            dto.carMake = car.getMake();
            dto.carModel = car.getModel();
            dto.carPlate = car.getPlate();
            dto.carYear = car.getBuildYear();
            dto.carSeats = car.getAvailableSeats();
            dto.smokingAllowed = car.getSmokingAllowed();
            dto.petsAllowed = car.getPetsAllowed();
        }
        
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{driverId}/reviews")
    public List<ReviewDto> getDriverReviews(@PathVariable Integer driverId) {
        List<Review> reviews = reviewRepo.findByRevieweeIdAndRevieweeTypeOrderByCreatedAtDesc(driverId, "DRIVER");
        return reviews.stream()
                .map(this::toReviewDto)
                .collect(Collectors.toList());
    }

    private ReviewDto toReviewDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.id = review.getId();
        dto.rideId = review.getRideId();
        dto.reviewerId = review.getReviewerId();
        dto.reviewerName = review.getReviewerName();
        dto.rating = review.getRating();
        dto.comment = review.getComment();
        dto.categories = review.getCategories();
        dto.createdAt = review.getCreatedAt();
        return dto;
    }
}

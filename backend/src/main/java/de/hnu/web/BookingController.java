package de.hnu.web;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import de.hnu.domain.Booking;
import de.hnu.domain.Car;
import de.hnu.domain.Person;
import de.hnu.domain.RideOffer;
import de.hnu.domain.enums.BookingStatus;
import de.hnu.domain.enums.PaymentStatus;
import de.hnu.repo.BookingRepository;
import de.hnu.repo.CarRepository;
import de.hnu.repo.PersonRepository;
import de.hnu.repo.RideOfferRepository;
import de.hnu.web.dto.BookingDto;
import de.hnu.web.dto.CreateBookingDto;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "http://localhost:4200")
public class BookingController {

    private final BookingRepository bookingRepo;
    private final RideOfferRepository rideOfferRepo;
    private final PersonRepository personRepo;
    private final CarRepository carRepo;

    public BookingController(
            BookingRepository bookingRepo,
            RideOfferRepository rideOfferRepo,
            PersonRepository personRepo,
            CarRepository carRepo) {
        this.bookingRepo = bookingRepo;
        this.rideOfferRepo = rideOfferRepo;
        this.personRepo = personRepo;
        this.carRepo = carRepo;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDto> getBooking(@PathVariable Integer id) {
        return bookingRepo.findById(id)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public List<BookingDto> getUserBookings(
            @PathVariable Integer userId,
            @RequestParam(required = false) String status) {
        
        List<Booking> bookings;
        
        if (status != null && !status.isEmpty()) {
            if ("upcoming".equalsIgnoreCase(status)) {
                bookings = bookingRepo.findByPassengerIdAndStatusIn(
                        userId,
                        List.of(BookingStatus.CONFIRMED, BookingStatus.UPCOMING, BookingStatus.PENDING)
                );
            } else if ("past".equalsIgnoreCase(status)) {
                bookings = bookingRepo.findByPassengerIdAndStatusIn(
                        userId,
                        List.of(BookingStatus.COMPLETED, BookingStatus.CANCELLED)
                );
            } else {
                try {
                    BookingStatus bookingStatus = BookingStatus.valueOf(status.toUpperCase());
                    bookings = bookingRepo.findByPassengerIdAndStatus(userId, bookingStatus);
                } catch (IllegalArgumentException e) {
                    bookings = bookingRepo.findByPassengerId(userId);
                }
            }
        } else {
            bookings = bookingRepo.findByPassengerId(userId);
        }
        
        return bookings.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(
            @RequestBody CreateBookingDto dto,
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Integer userId) {
        
        RideOffer offer = rideOfferRepo.findById(dto.rideOfferId).orElse(null);
        if (offer == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Person driver = null;
        Car car = null;
        
        if (offer.getDriverPersonId() != null) {
            driver = personRepo.findById(offer.getDriverPersonId()).orElse(null);
            if (driver != null && driver.getCarId() != null) {
                car = carRepo.findById(driver.getCarId()).orElse(null);
            }
        }
        
        Booking booking = new Booking();
        booking.setRideOfferId(dto.rideOfferId);
        booking.setPassengerId(userId);
        booking.setDepartureCity(offer.getDepartureCity());
        booking.setDestinationCity(offer.getDestinationCity());
        booking.setDepartureTime(offer.getDepartureTime());
        booking.setPickupLocation(dto.pickupLocation);
        booking.setDropoffLocation(dto.dropoffLocation);
        booking.setLuggageCount(dto.luggageCount);
        booking.setPet(dto.pet);
        booking.setKid(dto.kid);
        booking.setPricePerPerson(offer.getPricePerPerson());
        booking.setTotalPrice(offer.getPricePerPerson()); // Single passenger
        booking.setPaymentMethod(dto.paymentMethod);
        booking.setPaymentStatus(PaymentStatus.PENDING);
        booking.setStatus(BookingStatus.PENDING);
        
        if (driver != null) {
            booking.setDriverId(driver.getId());
            booking.setDriverName(driver.getName());
            booking.setDriverPhone(driver.getPhoneNumber());
        }
        
        if (car != null) {
            booking.setCarMake(car.getMake());
            booking.setCarModel(car.getModel());
            booking.setCarPlate(car.getPlate());
        }
        
        bookingRepo.save(booking);
        
        return ResponseEntity.ok(toDto(booking));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingDto> cancelBooking(@PathVariable Integer id) {
        return bookingRepo.findById(id)
                .map(booking -> {
                    booking.setStatus(BookingStatus.CANCELLED);
                    booking.setUpdatedAt(Instant.now());
                    bookingRepo.save(booking);
                    return ResponseEntity.ok(toDto(booking));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<BookingDto> confirmBooking(@PathVariable Integer id) {
        return bookingRepo.findById(id)
                .map(booking -> {
                    booking.setStatus(BookingStatus.CONFIRMED);
                    booking.setPaymentStatus(PaymentStatus.COMPLETED);
                    booking.setUpdatedAt(Instant.now());
                    bookingRepo.save(booking);
                    return ResponseEntity.ok(toDto(booking));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private BookingDto toDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.id = booking.getId();
        dto.rideId = booking.getRideId();
        dto.rideOfferId = booking.getRideOfferId();
        dto.departureCity = booking.getDepartureCity();
        dto.destinationCity = booking.getDestinationCity();
        dto.departureTime = booking.getDepartureTime();
        dto.pickupLocation = booking.getPickupLocation();
        dto.dropoffLocation = booking.getDropoffLocation();
        dto.pricePerPerson = booking.getPricePerPerson();
        dto.totalPrice = booking.getTotalPrice();
        dto.status = booking.getStatus() != null ? booking.getStatus().name() : null;
        dto.paymentMethod = booking.getPaymentMethod() != null ? booking.getPaymentMethod().name() : null;
        dto.paymentStatus = booking.getPaymentStatus() != null ? booking.getPaymentStatus().name() : null;
        dto.driverName = booking.getDriverName();
        dto.driverId = booking.getDriverId();
        dto.driverPhone = booking.getDriverPhone();
        dto.carMake = booking.getCarMake();
        dto.carModel = booking.getCarModel();
        dto.carPlate = booking.getCarPlate();
        dto.luggageCount = booking.getLuggageCount();
        dto.pet = booking.getPet();
        dto.kid = booking.getKid();
        return dto;
    }
}

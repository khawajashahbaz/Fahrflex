package de.hnu.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import de.hnu.domain.Booking;
import de.hnu.domain.enums.BookingStatus;
import de.hnu.domain.enums.PaymentStatus;
import de.hnu.repo.BookingRepository;
import de.hnu.web.dto.PaymentResultDto;
import de.hnu.web.dto.ProcessPaymentDto;
import de.hnu.web.dto.VerifyOtpDto;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {

    private final BookingRepository bookingRepo;
    
    // In-memory OTP storage (in production, use Redis or database)
    private final Map<Integer, String> otpStorage = new HashMap<>();

    public PaymentController(BookingRepository bookingRepo) {
        this.bookingRepo = bookingRepo;
    }

    @PostMapping("/process")
    public ResponseEntity<PaymentResultDto> processPayment(@RequestBody ProcessPaymentDto dto) {
        Booking booking = bookingRepo.findById(dto.bookingId).orElse(null);
        if (booking == null) {
            PaymentResultDto result = new PaymentResultDto();
            result.success = false;
            result.message = "Booking not found";
            return ResponseEntity.badRequest().body(result);
        }

        // Simulate payment processing
        booking.setPaymentMethod(dto.paymentMethod);
        booking.setPaymentStatus(PaymentStatus.PROCESSING);
        bookingRepo.save(booking);

        // Generate OTP for verification
        String otp = generateOtp();
        otpStorage.put(dto.bookingId, otp);
        
        // In production, send OTP via SMS/email
        System.out.println("Generated OTP for booking " + dto.bookingId + ": " + otp);

        PaymentResultDto result = new PaymentResultDto();
        result.success = true;
        result.message = "OTP sent to your registered phone number";
        result.requiresOtp = true;
        result.transactionId = "TXN_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        return ResponseEntity.ok(result);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<PaymentResultDto> verifyOtp(@RequestBody VerifyOtpDto dto) {
        Booking booking = bookingRepo.findById(dto.bookingId).orElse(null);
        if (booking == null) {
            PaymentResultDto result = new PaymentResultDto();
            result.success = false;
            result.message = "Booking not found";
            return ResponseEntity.badRequest().body(result);
        }

        String storedOtp = otpStorage.get(dto.bookingId);
        
        // Accept "123456" as a test OTP, or the actual generated OTP
        boolean isValidOtp = "123456".equals(dto.otp) || (storedOtp != null && storedOtp.equals(dto.otp));
        
        PaymentResultDto result = new PaymentResultDto();
        
        if (isValidOtp) {
            booking.setPaymentStatus(PaymentStatus.COMPLETED);
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepo.save(booking);
            
            otpStorage.remove(dto.bookingId);
            
            result.success = true;
            result.message = "Payment completed successfully";
            result.requiresOtp = false;
            result.transactionId = "TXN_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            
            return ResponseEntity.ok(result);
        } else {
            result.success = false;
            result.message = "Invalid OTP. Please try again.";
            result.requiresOtp = true;
            
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/refund/{bookingId}")
    public ResponseEntity<PaymentResultDto> refundPayment(@PathVariable Integer bookingId) {
        Booking booking = bookingRepo.findById(bookingId).orElse(null);
        if (booking == null) {
            PaymentResultDto result = new PaymentResultDto();
            result.success = false;
            result.message = "Booking not found";
            return ResponseEntity.badRequest().body(result);
        }

        booking.setPaymentStatus(PaymentStatus.REFUNDED);
        bookingRepo.save(booking);

        PaymentResultDto result = new PaymentResultDto();
        result.success = true;
        result.message = "Refund processed successfully";
        result.transactionId = "REF_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        return ResponseEntity.ok(result);
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}

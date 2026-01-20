import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RidesApi, PaymentMethod, RideOfferDetailDto } from '../../core/api/rides-api';

type PaymentStep = 'select' | 'card' | 'paypal' | 'otp' | 'success';

@Component({
  selector: 'app-payment',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './payment.html',
  styleUrls: ['./payment.scss'],
})
export class PaymentComponent implements OnInit {
  private api = inject(RidesApi);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  step: PaymentStep = 'select';
  selectedMethod: PaymentMethod = 'CARD';
  processing = false;
  error = '';
  
  // Ride details (passed from previous page or fetched)
  rideDetails: any = null;
  bookingId: string | null = null;
  transactionId: string | null = null;

  cardForm = this.fb.group({
    cardNumber: ['', [Validators.required, Validators.pattern(/^\d{16}$/)]],
    cardName: ['', Validators.required],
    expiryMonth: ['', [Validators.required, Validators.pattern(/^(0[1-9]|1[0-2])$/)]],
    expiryYear: ['', [Validators.required, Validators.pattern(/^\d{2}$/)]],
    cvv: ['', [Validators.required, Validators.pattern(/^\d{3,4}$/)]],
    saveCard: [false]
  });

  paypalForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });

  otpForm = this.fb.group({
    otp: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]]
  });

  ngOnInit(): void {
    const offerId = this.route.snapshot.queryParamMap.get('offerId');
    
    // Mock ride details - in real app would come from state or API
    this.rideDetails = {
      departureCity: 'Neu-Ulm',
      destinationCity: 'Munich',
      departureTime: '2026-01-20T08:30:00Z',
      pricePerPerson: 18.5,
      driverName: 'Max Mustermann',
      seatsBooked: 1,
      luggageCount: 1,
      pet: false,
      kid: false
    };
  }

  get totalAmount(): number {
    if (!this.rideDetails) return 0;
    let amount = this.rideDetails.pricePerPerson * this.rideDetails.seatsBooked;
    return Math.round(amount * 100) / 100;
  }

  selectPaymentMethod(method: PaymentMethod): void {
    this.selectedMethod = method;
    this.error = '';
  }

  proceedToPayment(): void {
    this.error = '';
    
    if (this.selectedMethod === 'CASH') {
      // For cash, skip to success
      this.step = 'success';
      this.bookingId = 'BOOK-' + Math.random().toString(36).substring(2, 10).toUpperCase();
    } else if (this.selectedMethod === 'CARD') {
      this.step = 'card';
    } else if (this.selectedMethod === 'PAYPAL') {
      this.step = 'paypal';
    }
  }

  submitCardPayment(): void {
    if (this.cardForm.invalid) {
      this.error = 'Please fill in all card details correctly';
      return;
    }

    this.processing = true;
    this.error = '';

    // Simulate payment processing
    setTimeout(() => {
      this.processing = false;
      this.step = 'otp';
    }, 1500);
  }

  submitPaypalPayment(): void {
    if (this.paypalForm.invalid) {
      this.error = 'Please enter your PayPal credentials';
      return;
    }

    this.processing = true;
    this.error = '';

    // Simulate PayPal login and redirect
    setTimeout(() => {
      this.processing = false;
      this.step = 'otp';
    }, 1500);
  }

  verifyOtp(): void {
    if (this.otpForm.invalid) {
      this.error = 'Please enter a valid 6-digit OTP';
      return;
    }

    this.processing = true;
    this.error = '';

    // Simulate OTP verification
    setTimeout(() => {
      this.processing = false;
      this.transactionId = 'TXN-' + Math.random().toString(36).substring(2, 12).toUpperCase();
      this.bookingId = 'BOOK-' + Math.random().toString(36).substring(2, 10).toUpperCase();
      this.step = 'success';
    }, 1000);
  }

  resendOtp(): void {
    // Simulate resending OTP
    alert('OTP resent to your registered mobile number');
  }

  goBack(): void {
    if (this.step === 'card' || this.step === 'paypal') {
      this.step = 'select';
    } else if (this.step === 'otp') {
      this.step = this.selectedMethod === 'CARD' ? 'card' : 'paypal';
    }
    this.error = '';
  }

  formatDate(iso: string): string {
    const d = new Date(iso);
    return d.toLocaleDateString('en-GB', {
      weekday: 'short',
      day: '2-digit',
      month: 'short',
      year: 'numeric'
    });
  }

  formatTime(iso: string): string {
    const d = new Date(iso);
    return d.toLocaleTimeString('en-GB', {
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  formatCardNumber(event: any): void {
    let value = event.target.value.replace(/\D/g, '');
    if (value.length > 16) value = value.substring(0, 16);
    event.target.value = value;
    this.cardForm.patchValue({ cardNumber: value });
  }
}

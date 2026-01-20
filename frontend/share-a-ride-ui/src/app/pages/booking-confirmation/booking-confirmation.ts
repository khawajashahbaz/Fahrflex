import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink, UrlSegment } from '@angular/router';
import { RidesApi, BookingDto } from '../../core/api/rides-api';

@Component({
  selector: 'app-booking-confirmation',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './booking-confirmation.html',
  styleUrls: ['./booking-confirmation.scss'],
})
export class BookingConfirmationComponent implements OnInit {
  private api = inject(RidesApi);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  
  booking: BookingDto | null = null;
  loading = true;
  showCancelModal = false;
  cancelling = false;

  ngOnInit(): void {
    const bookingId = this.route.snapshot.paramMap.get('id');
    const isCancelRoute = this.route.snapshot.url.some((s: UrlSegment) => s.path === 'cancel');
    
    if (bookingId) {
      this.loadBooking(bookingId);
      if (isCancelRoute) {
        this.showCancelModal = true;
      }
    }
  }

  private loadBooking(bookingId: string): void {
    // Mock data for demonstration
    setTimeout(() => {
      this.booking = {
        id: bookingId,
        rideId: 'ride_1',
        rideOfferId: 'offer_1',
        departureCity: 'Neu-Ulm',
        destinationCity: 'Munich',
        departureTime: '2026-01-20T08:30:00Z',
        pickupLocation: 'Neu-Ulm Hauptbahnhof',
        dropoffLocation: 'Munich Central Station',
        pricePerPerson: 18.5,
        totalPrice: 18.5,
        status: 'UPCOMING',
        paymentMethod: 'CARD',
        paymentStatus: 'COMPLETED',
        driverName: 'Max Mustermann',
        driverPhone: '+49 170 0000000',
        carMake: 'Volkswagen',
        carModel: 'Golf',
        carPlate: 'UL-AB-1234',
        luggageCount: 1,
        pet: false,
        kid: false
      };
      this.loading = false;
    }, 300);
  }

  formatDate(iso: string): string {
    const d = new Date(iso);
    return d.toLocaleDateString('en-GB', {
      weekday: 'long',
      day: '2-digit',
      month: 'long',
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

  openCancelModal(): void {
    this.showCancelModal = true;
  }

  closeCancelModal(): void {
    this.showCancelModal = false;
    // Remove /cancel from URL if present
    if (this.route.snapshot.url.some((s: UrlSegment) => s.path === 'cancel')) {
      this.router.navigate(['/booking', this.booking?.id]);
    }
  }

  confirmCancel(): void {
    if (!this.booking) return;
    
    this.cancelling = true;
    
    // Mock cancellation
    setTimeout(() => {
      if (this.booking) {
        this.booking.status = 'CANCELLED';
      }
      this.cancelling = false;
      this.showCancelModal = false;
    }, 1000);
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'UPCOMING': return 'status-upcoming';
      case 'COMPLETED': return 'status-completed';
      case 'CANCELLED': return 'status-cancelled';
      default: return '';
    }
  }
}

import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { RidesApi, BookingDto } from '../../core/api/rides-api';

type TabType = 'upcoming' | 'past';

@Component({
  selector: 'app-my-rides',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './my-rides.html',
  styleUrls: ['./my-rides.scss'],
})
export class MyRidesComponent implements OnInit {
  private api = inject(RidesApi);
  
  activeTab: TabType = 'upcoming';
  bookings: BookingDto[] = [];
  loading = true;
  error = '';
  
  // Mock passenger ID - in real app would come from auth
  private readonly passengerPersonId = 'person_passenger_1';

  ngOnInit(): void {
    this.loadBookings();
  }

  switchTab(tab: TabType): void {
    this.activeTab = tab;
  }

  get upcomingRides(): BookingDto[] {
    return this.bookings.filter(b => b.status === 'UPCOMING');
  }

  get pastRides(): BookingDto[] {
    return this.bookings.filter(b => b.status === 'COMPLETED' || b.status === 'CANCELLED');
  }

  get displayedRides(): BookingDto[] {
    return this.activeTab === 'upcoming' ? this.upcomingRides : this.pastRides;
  }

  private loadBookings(): void {
    this.loading = true;
    this.error = '';
    
    // Mock data for demonstration - in real app would call API
    setTimeout(() => {
      this.bookings = [
        {
          id: 'booking_1',
          rideId: 'ride_1',
          rideOfferId: 'offer_1',
          departureCity: 'Neu-Ulm',
          destinationCity: 'Munich',
          departureTime: '2026-01-20T08:30:00Z',
          pickupLocation: 'Neu-Ulm Hauptbahnhof',
          dropoffLocation: 'Munich Central',
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
        },
        {
          id: 'booking_2',
          rideId: 'ride_2',
          rideOfferId: 'offer_2',
          departureCity: 'Ulm',
          destinationCity: 'Stuttgart',
          departureTime: '2026-01-10T13:00:00Z',
          pickupLocation: 'Ulm Hauptbahnhof',
          dropoffLocation: 'Stuttgart Central',
          pricePerPerson: 22.0,
          totalPrice: 22.0,
          status: 'COMPLETED',
          paymentMethod: 'PAYPAL',
          paymentStatus: 'COMPLETED',
          driverName: 'Max Mustermann',
          driverPhone: '+49 170 0000000',
          carMake: 'Volkswagen',
          carModel: 'Golf',
          carPlate: 'UL-AB-1234',
          luggageCount: 2,
          pet: false,
          kid: false
        },
        {
          id: 'booking_3',
          rideId: 'ride_3',
          rideOfferId: 'offer_3',
          departureCity: 'Ulm',
          destinationCity: 'Munich',
          departureTime: '2026-01-05T07:15:00Z',
          pickupLocation: 'Ulm',
          dropoffLocation: 'Munich',
          pricePerPerson: 20.0,
          totalPrice: 20.0,
          status: 'CANCELLED',
          paymentMethod: 'CASH',
          paymentStatus: 'PENDING',
          driverName: 'Sophie Becker',
          driverPhone: '+49 170 1111111',
          carMake: 'BMW',
          carModel: '320i',
          carPlate: 'UL-CD-5678',
          luggageCount: 0,
          pet: false,
          kid: false
        }
      ];
      this.loading = false;
    }, 500);
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

  getStatusClass(status: string): string {
    switch (status) {
      case 'UPCOMING': return 'status-upcoming';
      case 'COMPLETED': return 'status-completed';
      case 'CANCELLED': return 'status-cancelled';
      default: return '';
    }
  }
}

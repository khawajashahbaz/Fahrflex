import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { RidesApi, ReviewDto } from '../../core/api/rides-api';

@Component({
  selector: 'app-driver-profile',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './driver-profile.html',
  styleUrls: ['./driver-profile.scss'],
})
export class DriverProfileComponent implements OnInit {
  private api = inject(RidesApi);
  private route = inject(ActivatedRoute);

  loading = true;
  driverId: string = '';
  
  driver: any = null;
  car: any = null;
  reviews: ReviewDto[] = [];
  averageRating = 0;

  ngOnInit(): void {
    this.driverId = this.route.snapshot.paramMap.get('driverId') || '';
    this.loadDriverProfile();
  }

  private loadDriverProfile(): void {
    // Mock data for demonstration
    setTimeout(() => {
      this.driver = {
        id: 'person_driver_1',
        name: 'Max Mustermann',
        gender: 'male',
        age: 29,
        homeCity: 'Neu-Ulm',
        bio: 'On-time driver, calm music, short breaks possible. I love road trips and enjoy meeting new people. Safety is my priority!',
        languages: ['DE', 'EN'],
        chatinessLevel: 2,
        overallKmCovered: 42000,
        memberSince: '2023-06-15',
        ridesCompleted: 156,
        verifications: ['email', 'phone', 'id']
      };

      this.car = {
        make: 'Volkswagen',
        model: 'Golf',
        buildYear: 2019,
        color: 'Black',
        plate: 'UL-AB-1234',
        availableSeats: 4,
        luggageSpace: 3,
        smokingAllowed: false,
        petsAllowed: true
      };

      this.reviews = [
        {
          id: 'rev_1',
          rideId: 'ride_1',
          reviewerId: 'person_passenger_2',
          driverId: this.driverId,
          rating: 5,
          comment: 'Great driver! Very punctual and the car was super clean. Would definitely ride again.',
          createdAt: '2026-01-10T14:00:00Z'
        },
        {
          id: 'rev_2',
          rideId: 'ride_2',
          reviewerId: 'person_passenger_3',
          driverId: this.driverId,
          rating: 4,
          comment: 'Pleasant ride, good music selection. A bit late to pickup but otherwise great.',
          createdAt: '2026-01-05T10:00:00Z'
        },
        {
          id: 'rev_3',
          rideId: 'ride_3',
          reviewerId: 'person_passenger_4',
          driverId: this.driverId,
          rating: 5,
          comment: 'Excellent! Safe driving and friendly conversation.',
          createdAt: '2025-12-28T16:00:00Z'
        }
      ];

      this.averageRating = this.reviews.reduce((sum, r) => sum + r.rating, 0) / this.reviews.length;
      
      this.loading = false;
    }, 400);
  }

  getChatinessLabel(level: number): string {
    const labels = ['Very quiet', 'Quiet', 'Moderate', 'Chatty', 'Very chatty'];
    return labels[level] || 'Moderate';
  }

  getLanguageName(code: string): string {
    const languages: Record<string, string> = {
      'DE': 'German',
      'EN': 'English',
      'AR': 'Arabic',
      'FR': 'French',
      'ES': 'Spanish',
      'IT': 'Italian'
    };
    return languages[code] || code;
  }

  formatDate(iso: string): string {
    const d = new Date(iso);
    return d.toLocaleDateString('en-GB', {
      day: '2-digit',
      month: 'short',
      year: 'numeric'
    });
  }

  formatMemberSince(iso: string): string {
    const d = new Date(iso);
    return d.toLocaleDateString('en-GB', {
      month: 'long',
      year: 'numeric'
    });
  }
}

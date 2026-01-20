import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RidesApi } from '../../core/api/rides-api';

@Component({
  selector: 'app-feedback',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './feedback.html',
  styleUrls: ['./feedback.scss'],
})
export class FeedbackComponent implements OnInit {
  private api = inject(RidesApi);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  rideId: string = '';
  loading = true;
  submitting = false;
  submitted = false;
  selectedRating = 0;
  hoveredRating = 0;

  // Ride details
  rideDetails = {
    departureCity: 'Ulm',
    destinationCity: 'Stuttgart',
    departureTime: '2026-01-10T13:00:00Z',
    driverName: 'Max Mustermann',
    driverId: 'person_driver_1'
  };

  // Feedback categories
  categories = [
    { id: 'punctuality', label: 'Punctuality', icon: '⏰', selected: false },
    { id: 'cleanliness', label: 'Car Cleanliness', icon: '🧹', selected: false },
    { id: 'driving', label: 'Safe Driving', icon: '🚗', selected: false },
    { id: 'communication', label: 'Good Communication', icon: '💬', selected: false },
    { id: 'friendly', label: 'Friendly', icon: '😊', selected: false },
    { id: 'comfortable', label: 'Comfortable Ride', icon: '✨', selected: false }
  ];

  feedbackForm = this.fb.group({
    comment: ['', [Validators.maxLength(500)]]
  });

  ngOnInit(): void {
    this.rideId = this.route.snapshot.paramMap.get('rideId') || '';
    
    // Simulate loading
    setTimeout(() => {
      this.loading = false;
    }, 300);
  }

  setRating(rating: number): void {
    this.selectedRating = rating;
  }

  setHoveredRating(rating: number): void {
    this.hoveredRating = rating;
  }

  toggleCategory(category: any): void {
    category.selected = !category.selected;
  }

  get selectedCategories(): string[] {
    return this.categories.filter(c => c.selected).map(c => c.id);
  }

  get ratingLabel(): string {
    const labels = ['', 'Poor', 'Fair', 'Good', 'Very Good', 'Excellent'];
    return labels[this.selectedRating] || '';
  }

  get canSubmit(): boolean {
    return this.selectedRating > 0;
  }

  submitFeedback(): void {
    if (!this.canSubmit) return;

    this.submitting = true;

    const feedback = {
      rideId: this.rideId,
      bookingId: this.rideId,
      driverId: this.rideDetails.driverId,
      rating: this.selectedRating,
      categories: this.selectedCategories,
      comment: this.feedbackForm.value.comment || ''
    };

    console.log('Submitting feedback:', feedback);

    // Simulate API call
    setTimeout(() => {
      this.submitting = false;
      this.submitted = true;
    }, 1000);
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
}

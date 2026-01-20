import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { RidesApi, CreateRideOfferDto, RideOfferSummaryDto } from '../../core/api/rides-api';

type PostRideStep = 'route' | 'stopovers' | 'schedule' | 'vehicle' | 'preferences' | 'pricing' | 'review';

interface StepInfo {
  id: PostRideStep;
  title: string;
}

interface UserCar {
  id: string;
  make: string;
  model: string;
  plate: string;
  availableSeats: number;
  luggageSpace: number;
  buildYear: number;
}

@Component({
  selector: 'app-post-ride',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './post-ride.html',
  styleUrls: ['./post-ride.scss']
})
export class PostRideComponent implements OnInit {
  currentStep: PostRideStep = 'route';
  
  steps: StepInfo[] = [
    { id: 'route', title: 'Route' },
    { id: 'stopovers', title: 'Stops' },
    { id: 'schedule', title: 'Schedule' },
    { id: 'vehicle', title: 'Vehicle' },
    { id: 'preferences', title: 'Preferences' },
    { id: 'pricing', title: 'Pricing' },
    { id: 'review', title: 'Review' }
  ];

  // Forms
  routeForm!: FormGroup;
  stopoversForm!: FormGroup;
  scheduleForm!: FormGroup;
  vehicleForm!: FormGroup;
  preferencesForm!: FormGroup;
  pricingForm!: FormGroup;

  // Data
  cities: string[] = [];
  userCars: UserCar[] = [];
  stopovers: string[] = [];
  suggestedPrice: number = 0;
  minDate: string = '';

  // State
  loading = false;
  submitting = false;
  error: string | null = null;
  successMessage: string | null = null;

  // Current user (simulated)
  currentUserId = 'person_driver_1';

  constructor(
    private fb: FormBuilder,
    private ridesApi: RidesApi,
    private router: Router
  ) {}

  ngOnInit() {
    this.initForms();
    this.loadCities();
    this.loadUserCars();
    this.setMinDate();
  }

  private setMinDate(): void {
    const today = new Date();
    this.minDate = today.toISOString().split('T')[0];
  }

  private initForms(): void {
    this.routeForm = this.fb.group({
      departureCity: ['', Validators.required],
      destinationCity: ['', Validators.required]
    });

    this.stopoversForm = this.fb.group({
      stops: ['']
    });

    this.scheduleForm = this.fb.group({
      departureDate: ['', Validators.required],
      departureTime: ['', Validators.required],
      flexibleTime: [false],
      flexibilityMinutes: [15]
    });

    this.vehicleForm = this.fb.group({
      carId: ['', Validators.required],
      seatsOffered: [2, [Validators.required, Validators.min(1), Validators.max(7)]],
      luggageSpace: [2, [Validators.required, Validators.min(0), Validators.max(5)]]
    });

    this.preferencesForm = this.fb.group({
      smokingAllowed: [false],
      petsAllowed: [false],
      musicAllowed: [true],
      chatLevel: [2],
      additionalNotes: ['']
    });

    this.pricingForm = this.fb.group({
      pricePerPerson: [0, [Validators.required, Validators.min(1)]],
      acceptCash: [true],
      acceptCard: [true],
      acceptPaypal: [false]
    });
  }

  private loadCities(): void {
    this.ridesApi.getCities().subscribe({
      next: (cities: string[]) => {
        this.cities = cities;
      },
      error: () => {
        this.cities = ['Berlin', 'Munich', 'Stuttgart', 'Ulm', 'Neu-Ulm', 'Frankfurt', 'Hamburg', 'Cologne', 'Düsseldorf', 'Nuremberg', 'Augsburg'];
      }
    });
  }

  private loadUserCars(): void {
    // In a real app, this would fetch from an API
    this.userCars = [
      {
        id: 'car_1',
        make: 'Volkswagen',
        model: 'Golf',
        plate: 'UL-AB-1234',
        availableSeats: 4,
        luggageSpace: 3,
        buildYear: 2019
      },
      {
        id: 'car_2',
        make: 'BMW',
        model: '3 Series',
        plate: 'M-XY-5678',
        availableSeats: 4,
        luggageSpace: 4,
        buildYear: 2021
      }
    ];

    if (this.userCars.length > 0) {
      this.selectCar(this.userCars[0]);
    }
  }

  // Navigation
  get currentStepIndex(): number {
    return this.steps.findIndex(s => s.id === this.currentStep);
  }

  get progressPercentage(): number {
    return ((this.currentStepIndex + 1) / this.steps.length) * 100;
  }

  isStepCompleted(step: PostRideStep): boolean {
    const stepIndex = this.steps.findIndex(s => s.id === step);
    return stepIndex < this.currentStepIndex;
  }

  isStepActive(step: PostRideStep): boolean {
    return step === this.currentStep;
  }

  canNavigateToStep(step: PostRideStep): boolean {
    const targetIndex = this.steps.findIndex(s => s.id === step);
    return targetIndex <= this.currentStepIndex;
  }

  canProceed(): boolean {
    switch (this.currentStep) {
      case 'route':
        return this.routeForm.valid && 
               this.routeForm.value.departureCity !== this.routeForm.value.destinationCity;
      case 'stopovers':
        return true; // Optional step
      case 'schedule':
        return this.scheduleForm.valid;
      case 'vehicle':
        return this.vehicleForm.valid;
      case 'preferences':
        return true; // Optional step
      case 'pricing':
        return this.pricingForm.valid && 
               (this.pricingForm.value.acceptCash || 
                this.pricingForm.value.acceptCard || 
                this.pricingForm.value.acceptPaypal);
      case 'review':
        return true;
      default:
        return false;
    }
  }

  nextStep(): void {
    if (!this.canProceed()) return;

    const stepIndex = this.currentStepIndex;
    if (stepIndex < this.steps.length - 1) {
      this.currentStep = this.steps[stepIndex + 1].id;

      // Calculate suggested price when reaching pricing step
      if (this.currentStep === 'pricing') {
        this.calculateSuggestedPrice();
      }
    }
  }

  prevStep(): void {
    const stepIndex = this.currentStepIndex;
    if (stepIndex > 0) {
      this.currentStep = this.steps[stepIndex - 1].id;
    }
  }

  goToStep(step: PostRideStep): void {
    if (this.canNavigateToStep(step)) {
      this.currentStep = step;
    }
  }

  // Route actions
  swapCities(): void {
    const dep = this.routeForm.value.departureCity;
    const dst = this.routeForm.value.destinationCity;
    this.routeForm.patchValue({
      departureCity: dst,
      destinationCity: dep
    });
  }

  // Stopovers actions
  addStopover(): void {
    this.stopovers.push('');
  }

  removeStopover(index: number): void {
    this.stopovers.splice(index, 1);
  }

  // Schedule actions
  setFlexibility(minutes: number): void {
    this.scheduleForm.patchValue({ flexibilityMinutes: minutes });
  }

  // Vehicle actions
  selectCar(car: UserCar): void {
    this.vehicleForm.patchValue({
      carId: car.id,
      seatsOffered: Math.min(2, car.availableSeats),
      luggageSpace: car.luggageSpace
    });
  }

  getSelectedCar(): UserCar | undefined {
    return this.userCars.find(c => c.id === this.vehicleForm.value.carId);
  }

  incrementSeats(): void {
    const car = this.getSelectedCar();
    const current = this.vehicleForm.value.seatsOffered;
    if (car && current < car.availableSeats) {
      this.vehicleForm.patchValue({ seatsOffered: current + 1 });
    }
  }

  decrementSeats(): void {
    const current = this.vehicleForm.value.seatsOffered;
    if (current > 1) {
      this.vehicleForm.patchValue({ seatsOffered: current - 1 });
    }
  }

  incrementLuggage(): void {
    const current = this.vehicleForm.value.luggageSpace;
    if (current < 5) {
      this.vehicleForm.patchValue({ luggageSpace: current + 1 });
    }
  }

  decrementLuggage(): void {
    const current = this.vehicleForm.value.luggageSpace;
    if (current > 0) {
      this.vehicleForm.patchValue({ luggageSpace: current - 1 });
    }
  }

  // Preferences
  getChatLevelLabel(level: number): string {
    const labels = ['Silent traveler', 'Quiet rider', 'Moderate chatter', 'Social butterfly', 'Life of the party'];
    return labels[level] || 'Moderate chatter';
  }

  // Pricing
  calculateSuggestedPrice(): void {
    const distances: { [key: string]: { [key: string]: number } } = {
      'Ulm': { 'Munich': 150, 'Stuttgart': 90, 'Berlin': 620, 'Frankfurt': 220, 'Nuremberg': 160, 'Augsburg': 80 },
      'Neu-Ulm': { 'Munich': 145, 'Stuttgart': 95, 'Berlin': 615, 'Frankfurt': 225, 'Nuremberg': 155, 'Augsburg': 75 },
      'Munich': { 'Ulm': 150, 'Stuttgart': 230, 'Berlin': 585, 'Frankfurt': 390, 'Nuremberg': 170, 'Augsburg': 70 },
      'Stuttgart': { 'Ulm': 90, 'Munich': 230, 'Berlin': 630, 'Frankfurt': 210, 'Nuremberg': 210 },
      'Augsburg': { 'Munich': 70, 'Ulm': 80, 'Nuremberg': 150 }
    };

    const from = this.routeForm.value.departureCity;
    const to = this.routeForm.value.destinationCity;

    let distance = 100;
    if (distances[from]?.[to]) {
      distance = distances[from][to];
    } else if (distances[to]?.[from]) {
      distance = distances[to][from];
    }

    // ~0.08€ per km
    this.suggestedPrice = Math.round(distance * 0.08 * 100) / 100;

    if (!this.pricingForm.value.pricePerPerson || this.pricingForm.value.pricePerPerson === 0) {
      this.pricingForm.patchValue({ pricePerPerson: this.suggestedPrice });
    }
  }

  applySuggestedPrice(): void {
    this.pricingForm.patchValue({ pricePerPerson: this.suggestedPrice });
  }

  getPaymentMethods(): string[] {
    const methods: string[] = [];
    if (this.pricingForm.value.acceptCash) methods.push('Cash');
    if (this.pricingForm.value.acceptCard) methods.push('Card');
    if (this.pricingForm.value.acceptPaypal) methods.push('PayPal');
    return methods;
  }

  // Review
  getFormattedDateTime(): string {
    const date = this.scheduleForm.value.departureDate;
    const time = this.scheduleForm.value.departureTime;
    if (!date || !time) return '';

    const dateObj = new Date(`${date}T${time}`);
    return dateObj.toLocaleString('en-US', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  // Submit
  submitRide(): void {
    if (this.submitting) return;

    this.submitting = true;
    this.error = null;

    const dateTime = new Date(`${this.scheduleForm.value.departureDate}T${this.scheduleForm.value.departureTime}`);

    const filteredStopovers = this.stopovers.filter(s => s.trim() !== '');

    const rideOffer: CreateRideOfferDto = {
      departureCity: this.routeForm.value.departureCity,
      destinationCity: this.routeForm.value.destinationCity,
      departureTime: dateTime.toISOString(),
      seatsAvailable: this.vehicleForm.value.seatsOffered,
      pricePerPerson: this.pricingForm.value.pricePerPerson,
      luggageCount: this.vehicleForm.value.luggageSpace,
      driverPersonId: this.currentUserId,
      carId: this.vehicleForm.value.carId,
      smokingAllowed: this.preferencesForm.value.smokingAllowed,
      petsAllowed: this.preferencesForm.value.petsAllowed,
      musicAllowed: this.preferencesForm.value.musicAllowed,
      chatLevel: this.preferencesForm.value.chatLevel,
      additionalNotes: this.preferencesForm.value.additionalNotes,
      flexibleTime: this.scheduleForm.value.flexibleTime,
      flexibilityMinutes: this.scheduleForm.value.flexibilityMinutes,
      stops: filteredStopovers,
      acceptedPaymentMethods: this.getPaymentMethods()
    };

    this.ridesApi.createRideOffer(rideOffer).subscribe({
      next: (created: RideOfferSummaryDto) => {
        this.submitting = false;
        this.successMessage = 'Your ride has been posted successfully! Redirecting...';

        setTimeout(() => {
          this.router.navigate(['/my-rides']);
        }, 2000);
      },
      error: (err) => {
        this.submitting = false;
        this.error = err?.error?.message ?? err?.message ?? 'Failed to post ride. Please try again.';
      }
    });
  }
}

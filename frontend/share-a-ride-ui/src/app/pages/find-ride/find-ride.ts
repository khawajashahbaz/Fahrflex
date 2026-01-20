import { Component, OnDestroy, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { Subscription, debounceTime, distinctUntilChanged, interval, startWith, switchMap, takeWhile, timeout, forkJoin } from 'rxjs';

import { RidesApi, RideOfferSummaryDto, CreateRideRequestDto, RideRequest, RideOfferDetailDto } from '../../core/api/rides-api';

type UiState = 'idle' | 'loading' | 'done' | 'error';

@Component({
  selector: 'app-find-ride',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './find-ride.html',
  styleUrl: './find-ride.scss',
})
export class FindRideComponent implements OnInit, OnDestroy {
  state: UiState = 'idle';
  errorMessage = '';
  detailsExpandedOfferId: string | null = null;
  joinExpandedOfferId: string | null = null;
  detailsById: Record<string, any> = {};
  detailsLoadingById: Record<string, boolean> = {};
  detailsErrorById: Record<string, string> = {};
  allResults: RideOfferSummaryDto[] = [];
  results: RideOfferSummaryDto[] = [];
  joinFormsByOfferId: Record<string, ReturnType<FormBuilder['group']>> = {};
  joinStateByOfferId: Record<string, { stage: 'idle' | 'requesting' | 'waiting' | 'accepted' | 'error'; message?: string; requestId?: string; rideId?: string }> = {};

  // Sorting
  sortBy: 'departure' | 'price' | 'seats' = 'departure';

  // City autocomplete
  availableCities: string[] = ['Ulm', 'Neu-Ulm', 'Munich', 'Stuttgart', 'Berlin', 'Frankfurt', 'Hamburg', 'Cologne', 'Düsseldorf', 'Augsburg', 'Nuremberg'];
  filteredDepartureCities: string[] = [];
  filteredDestinationCities: string[] = [];
  showDepartureDropdown = false;
  showDestinationDropdown = false;

  // For the prototype: choose a passenger that exists in your seed data
  private readonly passengerPersonId = 'person_passenger_1';
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  private sub = new Subscription();

  // Search criteria -> triggers backend call
  searchForm = this.fb.group({
    departureCity: [''],
    destinationCity: [''],
    date: [''],
  });

  // Filters -> client-side filtering (matching Balsamiq design)
  filterForm = this.fb.group({
    minSeats: [1],
    maxPrice: [100],
    timeSlot: [''],      // 'morning', 'afternoon', 'evening', ''
    // "I am" filters
    isWoman: [false],
    hasChild: [false],
    hasPet: [false],
    hasLuggage: [false],
    // Preferences
    noSmoking: [false],
    petsAllowed: [false],
    musicAllowed: [false],
  });

  constructor(private api: RidesApi, private route: ActivatedRoute, private cdr: ChangeDetectorRef) { }

  ngOnInit(): void {
    // Load cities from API
    this.loadCities();

    // Prefill from query params
    this.sub.add(
      this.route.queryParamMap.subscribe((p) => {
        this.searchForm.patchValue(
          {
            departureCity: p.get('departureCity') ?? '',
            destinationCity: p.get('destinationCity') ?? '',
            date: p.get('date') ?? '',
          },
          { emitEvent: false }
        );

        // run once on init if dep+dst present
        this.maybeSearch();
      })
    );

    // Search-as-you-type (debounced) for departure/destination/date
    this.sub.add(
      this.searchForm.valueChanges
        .pipe(
          debounceTime(300),
          distinctUntilChanged((a, b) => JSON.stringify(a) === JSON.stringify(b))
        )
        .subscribe(() => this.maybeSearch())
    );

    // Filters apply immediately client-side
    this.sub.add(this.filterForm.valueChanges.subscribe(() => this.applyClientFilters()));
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  private loadCities(): void {
    this.api.getCities().subscribe({
      next: (cities) => {
        if (cities && cities.length > 0) {
          this.availableCities = cities;
        }
      },
      error: () => {
        // Keep default cities
      }
    });
  }

  triggerSearch(): void {
    this.maybeSearch();
  }

  private maybeSearch(): void {
    const v = this.searchForm.value;
    const dep = (v.departureCity ?? '').trim();
    const dst = (v.destinationCity ?? '').trim();
    const date = (v.date ?? '').trim() ? (v.date ?? '').trim() : undefined;

    if (!dep || !dst) {
      this.state = 'idle';
      this.allResults = [];
      this.results = [];
      return;
    }

    this.state = 'loading';
    this.errorMessage = '';
    this.allResults = [];
    this.results = [];

    this.api.searchRideOffers(dep, dst, date).pipe(timeout(7000)).subscribe({
      next: (rows) => {
        this.allResults = rows ?? [];
        this.state = 'done';
        this.applyClientFilters();
        this.detailsExpandedOfferId = null;
        this.joinExpandedOfferId = null;
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.state = 'error';
        this.errorMessage =
          err?.error?.message ??
          err?.message ??
          'Search failed. Ensure backend is running on http://localhost:8080.';
        this.cdr.markForCheck();
      },
    });
  }

  private applyClientFilters(): void {
    if (this.state !== 'done') return;

    const f = this.filterForm.value;
    const minSeats = Number(f.minSeats ?? 1);
    const maxPrice = Number(f.maxPrice ?? 100);
    const timeSlot = f.timeSlot ?? '';
    const noSmoking = f.noSmoking;
    const petsAllowed = f.petsAllowed;
    const hasPet = f.hasPet;

    let filtered = [...this.allResults];

    // Seats filter
    filtered = filtered.filter((r) => (r.seatsAvailable ?? 0) >= minSeats);

    // Price filter
    filtered = filtered.filter((r) => (r.pricePerPerson ?? 0) <= maxPrice);

    // Time slot filter
    if (timeSlot) {
      filtered = filtered.filter((r) => {
        if (!r.departureTime) return true;
        const hour = new Date(r.departureTime).getHours();
        if (timeSlot === 'morning') return hour >= 6 && hour < 12;
        if (timeSlot === 'afternoon') return hour >= 12 && hour < 18;
        if (timeSlot === 'evening') return hour >= 18 || hour < 6;
        return true;
      });
    }

    // Smoking filter
    if (noSmoking) {
      filtered = filtered.filter((r) => r.smokingAllowed !== true);
    }

    // Pets filter (if user has pet, only show rides that allow pets)
    if (hasPet || petsAllowed) {
      filtered = filtered.filter((r) => r.petsAllowed === true);
    }

    // Sort results
    this.sortResults(filtered);
  }

  sortResults(data?: RideOfferSummaryDto[]): void {
    const toSort = data ?? this.results;

    switch (this.sortBy) {
      case 'departure':
        toSort.sort((a, b) => {
          const ta = a.departureTime ? new Date(a.departureTime).getTime() : 0;
          const tb = b.departureTime ? new Date(b.departureTime).getTime() : 0;
          return ta - tb;
        });
        break;
      case 'price':
        toSort.sort((a, b) => (a.pricePerPerson ?? 0) - (b.pricePerPerson ?? 0));
        break;
      case 'seats':
        toSort.sort((a, b) => (b.seatsAvailable ?? 0) - (a.seatsAvailable ?? 0));
        break;
    }

    this.results = [...toSort];
    this.cdr.markForCheck();
  }

  clearFilters(): void {
    this.filterForm.reset({
      minSeats: 1,
      maxPrice: 100,
      timeSlot: '',
      isWoman: false,
      hasChild: false,
      hasPet: false,
      hasLuggage: false,
      noSmoking: false,
      petsAllowed: false,
      musicAllowed: false,
    });
  }

  // Format helpers
  formatTime(iso?: string): string {
    if (!iso) return '';
    const d = new Date(iso);
    return d.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', hour12: false });
  }

  formatDate(iso?: string): string {
    if (!iso) return '';
    const d = new Date(iso);
    return d.toLocaleDateString('en-US', { weekday: 'short', month: 'short', day: 'numeric' });
  }

  formatArrivalTime(departureIso?: string, ride?: RideOfferSummaryDto): string {
    if (!departureIso) return '';
    const d = new Date(departureIso);
    // Estimate based on common distances (simplified)
    const durationMinutes = this.getDurationMinutes(ride);
    d.setMinutes(d.getMinutes() + durationMinutes);
    return d.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', hour12: false });
  }

  estimateDuration(ride?: RideOfferSummaryDto): string {
    const minutes = this.getDurationMinutes(ride);
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    if (hours === 0) return `${mins}min`;
    if (mins === 0) return `${hours}h`;
    return `${hours}h ${mins}min`;
  }

  private getDurationMinutes(ride?: RideOfferSummaryDto): number {
    if (!ride) return 60;
    // Simple estimation based on city pairs
    const routes: Record<string, number> = {
      'Ulm-Munich': 90, 'Munich-Ulm': 90,
      'Ulm-Stuttgart': 60, 'Stuttgart-Ulm': 60,
      'Munich-Stuttgart': 140, 'Stuttgart-Munich': 140,
      'Berlin-Munich': 360, 'Munich-Berlin': 360,
      'Frankfurt-Munich': 240, 'Munich-Frankfurt': 240,
    };
    const key = `${ride.departureCity}-${ride.destinationCity}`;
    return routes[key] ?? 90;
  }

  getChatLevel(level?: number): string {
    const levels = ['Very quiet', 'Quiet', 'Moderate', 'Chatty', 'Very chatty'];
    return levels[level ?? 2] ?? 'Moderate';
  }

  // Swap cities
  swapCities(): void {
    const dep = this.searchForm.value.departureCity;
    const dst = this.searchForm.value.destinationCity;
    this.searchForm.patchValue({
      departureCity: dst,
      destinationCity: dep,
    });
  }

  // City autocomplete methods
  onDepartureInput(event: Event): void {
    const value = (event.target as HTMLInputElement).value.toLowerCase();
    this.filteredDepartureCities = this.availableCities.filter(
      city => city.toLowerCase().includes(value) && city.toLowerCase() !== value
    );
    this.showDepartureDropdown = this.filteredDepartureCities.length > 0 && value.length > 0;
    this.cdr.markForCheck();
  }

  onDestinationInput(event: Event): void {
    const value = (event.target as HTMLInputElement).value.toLowerCase();
    this.filteredDestinationCities = this.availableCities.filter(
      city => city.toLowerCase().includes(value) && city.toLowerCase() !== value
    );
    this.showDestinationDropdown = this.filteredDestinationCities.length > 0 && value.length > 0;
    this.cdr.markForCheck();
  }

  selectDepartureCity(city: string): void {
    this.searchForm.patchValue({ departureCity: city });
    this.showDepartureDropdown = false;
    this.cdr.markForCheck();
  }

  selectDestinationCity(city: string): void {
    this.searchForm.patchValue({ destinationCity: city });
    this.showDestinationDropdown = false;
    this.cdr.markForCheck();
  }

  hideDepartureDropdown(): void {
    setTimeout(() => {
      this.showDepartureDropdown = false;
      this.cdr.markForCheck();
    }, 200);
  }

  hideDestinationDropdown(): void {
    setTimeout(() => {
      this.showDestinationDropdown = false;
      this.cdr.markForCheck();
    }, 200);
  }

  // Details
  toggleDetails(offerId: string): void {
    if (this.detailsExpandedOfferId === offerId) {
      this.detailsExpandedOfferId = null;
      this.cdr.markForCheck();
      return;
    }
    this.detailsExpandedOfferId = offerId;
    this.loadDetails(offerId);
    this.cdr.markForCheck();
  }

  private loadDetails(offerId: string): void {
    if (this.detailsById[offerId] || this.detailsLoadingById[offerId]) return;

    this.detailsLoadingById[offerId] = true;
    this.detailsErrorById[offerId] = '';

    this.api.getRideOfferDetail(offerId).subscribe({
      next: (dto) => {
        this.detailsById[offerId] = dto;
        this.detailsLoadingById[offerId] = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.detailsLoadingById[offerId] = false;
        this.detailsErrorById[offerId] =
          err?.error?.message ?? err?.message ?? 'Failed to load details.';
        this.cdr.markForCheck();
      },
    });
  }

  // Booking
  openBooking(ride: RideOfferSummaryDto): void {
    this.joinExpandedOfferId = ride.id;
    this.detailsExpandedOfferId = null;
    this.getJoinForm(ride);
    this.cdr.markForCheck();
  }

  getJoinForm(offer: RideOfferSummaryDto) {
    const id = offer.id;

    if (!this.joinFormsByOfferId[id]) {
      this.joinFormsByOfferId[id] = this.fb.group({
        pickupLocation: [offer.departureCity],
        dropoffLocation: [offer.destinationCity],
        seatsNeeded: [1],
        luggageCount: [0],
        pet: [false],
        kid: [false],
        paymentMethod: ['CASH'],
      });

      this.joinStateByOfferId[id] = { stage: 'idle' };
    }

    return this.joinFormsByOfferId[id];
  }

  cancelJoin(offerId: string): void {
    if (this.joinExpandedOfferId === offerId) this.joinExpandedOfferId = null;
    this.cdr.markForCheck();
  }

  submitJoin(offer: RideOfferSummaryDto): void {
    const form = this.getJoinForm(offer);
    const id = offer.id;
    const v = form.value as any;

    const dto: CreateRideRequestDto = {
      rideOfferId: id,
      personId: this.passengerPersonId,
      pickupLocation: (v.pickupLocation ?? '').trim(),
      dropoffLocation: (v.dropoffLocation ?? '').trim(),
      luggageCount: Number(v.luggageCount ?? 0),
      pet: Boolean(v.pet),
      kid: Boolean(v.kid),
      paymentMethod: (v.paymentMethod ?? 'CASH'),
    };

    if (!dto.pickupLocation || !dto.dropoffLocation) {
      this.joinStateByOfferId[id] = { stage: 'error', message: 'Pickup and dropoff are required.' };
      this.cdr.markForCheck();
      return;
    }

    this.joinStateByOfferId[id] = { stage: 'requesting', message: 'Creating request…' };
    this.cdr.markForCheck();

    this.api.createRideRequest(dto).subscribe({
      next: (rr) => {
        this.joinStateByOfferId[id] = {
          stage: rr.status === 'ACCEPTED' ? 'accepted' : 'waiting',
          message: rr.status === 'ACCEPTED' ? 'Accepted!' : 'Waiting for driver acceptance…',
          requestId: rr.id,
          rideId: rr.rideId,
        };
        this.cdr.markForCheck();

        if (rr.status === 'ACCEPTED') {
          this.onAccepted(offer, rr);
        } else {
          this.pollRideRequest(offer, rr.id);
        }
      },
      error: (err) => {
        this.joinStateByOfferId[id] = {
          stage: 'error',
          message: err?.error?.message ?? err?.message ?? 'Failed to create ride request.',
        };
        this.cdr.markForCheck();
      },
    });
  }

  private pollRideRequest(offer: RideOfferSummaryDto, requestId: string): void {
    const offerId = offer.id;

    interval(800)
      .pipe(
        startWith(0),
        switchMap(() => this.api.getRideRequest(requestId)),
        takeWhile((rr) => rr.status === 'PENDING', true),
        timeout(20000)
      )
      .subscribe({
        next: (rr) => {
          if (rr.status === 'PENDING') {
            this.joinStateByOfferId[offerId] = {
              stage: 'waiting',
              message: 'Waiting for driver acceptance…',
              requestId,
              rideId: rr.rideId,
            };
            this.cdr.markForCheck();
            return;
          }

          if (rr.status === 'REJECTED') {
            this.joinStateByOfferId[offerId] = { stage: 'error', message: 'Request was rejected.' };
            this.cdr.markForCheck();
            return;
          }

          this.joinStateByOfferId[offerId] = {
            stage: 'accepted',
            message: 'Accepted! Redirecting to payment…',
            requestId,
            rideId: rr.rideId,
          };
          this.cdr.markForCheck();
          this.onAccepted(offer, rr);
        },
        error: () => {
          this.joinStateByOfferId[offerId] = {
            stage: 'error',
            message: 'Polling timed out. Please try again.',
          };
          this.cdr.markForCheck();
        },
      });
  }

  private onAccepted(offer: RideOfferSummaryDto, rr: RideRequest): void {
    const form = this.getJoinForm(offer);
    const v = form.value as any;

    const seatCost = 1 + (v.pet ? 1 : 0) + (v.kid ? 1 : 0);
    const luggageUsed = Number(v.luggageCount ?? 0);

    offer.seatsAvailable = Math.max(0, (offer.seatsAvailable ?? 0) - seatCost);
    offer.luggageCount = Math.max(0, (offer.luggageCount ?? 0) - luggageUsed);

    this.cdr.markForCheck();

    // Navigate to payment
    setTimeout(() => {
      this.router.navigate(['/payment'], {
        queryParams: { offerId: offer.id, rideId: rr.rideId }
      });
    }, 1500);
  }

  viewDriverProfile(driverId: string): void {
    this.router.navigate(['/driver', driverId]);
  }
}

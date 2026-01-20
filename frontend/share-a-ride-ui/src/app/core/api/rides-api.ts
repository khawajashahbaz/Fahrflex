import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';

export type PaymentMethod = 'CASH' | 'PAYPAL' | 'CARD';
export type BookingStatus = 'UPCOMING' | 'COMPLETED' | 'CANCELLED';

export interface RideOfferSummaryDto {
  id: string;
  departureCity: string;
  destinationCity: string;
  departureTime: string;

  seatsAvailable: number;
  luggageCount: number;
  pricePerPerson: number;

  driverName?: string;
  driverId?: string;
  driverPersonId?: string;
  driverChatinessLevel?: number;
  driverOverallKmCovered?: number;
  carMake?: string;
  carModel?: string;
  smokingAllowed?: boolean;
  petsAllowed?: boolean;
  musicAllowed?: boolean;
  additionalNotes?: string;
}

export interface CreateRideOfferDto {
  departureCity: string;
  destinationCity: string;
  departureTime: string;
  seatsAvailable: number;
  pricePerPerson: number;
  luggageCount: number;
  driverPersonId: string;
  carId?: string;
  smokingAllowed?: boolean;
  petsAllowed?: boolean;
  musicAllowed?: boolean;
  chatLevel?: number;
  additionalNotes?: string;
  flexibleTime?: boolean;
  flexibilityMinutes?: number;
  stops?: string[];
  acceptedPaymentMethods?: string[];
}

export interface RideOfferDetailDto {
  offer: any;
  driver?: any;
  car?: any;
  insurance?: any;
}

export interface CreateRideRequestDto {
  rideOfferId: string;
  personId: string;
  pickupLocation: string;
  dropoffLocation: string;
  luggageCount: number;
  pet: boolean;
  kid: boolean;
  paymentMethod: PaymentMethod;
}

export interface RideRequest {
  id: string;
  rideOfferId: string;
  personId: string;

  pickupLocation: string;
  dropoffLocation: string;

  luggageCount: number;
  pet: boolean;
  kid: boolean;

  paymentMethod: PaymentMethod;

  status: 'PENDING' | 'ACCEPTED' | 'REJECTED';
  rideId?: string;
  passengerId?: string;
}

export interface RideHistoryDto {
  rideId: string;
  departureCity?: string;
  destinationCity?: string;
  departureTime?: string;
  role: 'DRIVER' | 'PASSENGER';
  status?: BookingStatus;
  pricePerPerson?: number;
  driverName?: string;
  carMake?: string;
  carModel?: string;
}

export interface BookingDto {
  id: string;
  rideId: string;
  rideOfferId: string;
  departureCity: string;
  destinationCity: string;
  departureTime: string;
  pickupLocation: string;
  dropoffLocation: string;
  pricePerPerson: number;
  totalPrice: number;
  status: BookingStatus;
  paymentMethod: PaymentMethod;
  paymentStatus: 'PENDING' | 'COMPLETED';
  driverName?: string;
  driverPhone?: string;
  carMake?: string;
  carModel?: string;
  carPlate?: string;
  luggageCount: number;
  pet: boolean;
  kid: boolean;
}

export interface ChatMessage {
  id: string;
  senderId: string;
  senderName: string;
  content: string;
  message?: string; // alias for content
  receiverId?: string;
  timestamp: string;
  rideId: string;
  isOwn?: boolean;
}

export interface ReviewDto {
  id?: string;
  rideId: string;
  bookingId?: string;
  reviewerId?: string;
  reviewerName?: string;
  driverId?: string;
  rating: number;
  comment: string;
  categories?: string[];
  createdAt?: string;
}

export interface DriverProfileDto {
  id: string;
  name: string;
  profilePicture?: string;
  bio?: string;
  homeCity?: string;
  age?: number;
  languages?: string[];
  chatinessLevel?: number;
  overallKmCovered?: number;
  totalRides?: number;
  averageRating?: number;
  totalReviews?: number;
  carMake?: string;
  carModel?: string;
  carPlate?: string;
  carYear?: number;
  carSeats?: number;
  smokingAllowed?: boolean;
  petsAllowed?: boolean;
}

export interface PaymentResultDto {
  success: boolean;
  message: string;
  requiresOtp?: boolean;
  transactionId?: string;
}

@Injectable({ providedIn: 'root' })
export class RidesApi {
  private readonly baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  searchRideOffers(departureCity: string, destinationCity: string, date?: string) {
    let params = new HttpParams()
      .set('departureCity', departureCity)
      .set('destinationCity', destinationCity);

    if (date) params = params.set('date', date);

    return this.http.get<RideOfferSummaryDto[]>(`${this.baseUrl}/rideoffers/search`, { params });
  }

  getRideOfferDetail(id: string) {
    return this.http.get<RideOfferDetailDto>(`${this.baseUrl}/rideoffers/${id}`);
  }

  createRideRequest(dto: CreateRideRequestDto) {
    return this.http.post<RideRequest>(`${this.baseUrl}/riderequests`, dto);
  }

  getRideRequest(id: string) {
    return this.http.get<RideRequest>(`${this.baseUrl}/riderequests/${id}`);
  }

  getRide(id: string) {
    return this.http.get<any>(`${this.baseUrl}/rides/${id}`);
  }

  getRidePassengers(rideId: string) {
    return this.http.get<any[]>(`${this.baseUrl}/rides/${rideId}/passengers`);
  }

  getPersonRides(personId: string) {
    return this.http.get<RideHistoryDto[]>(`${this.baseUrl}/persons/${personId}/rides`);
  }

  // City autocomplete - returns available cities from ride offers
  getCities(query?: string): Observable<string[]> {
    let params = new HttpParams();
    if (query) {
      params = params.set('query', query);
    }
    return this.http.get<string[]>(`${this.baseUrl}/cities`, { params });
  }

  // Get booking details
  getBooking(bookingId: string): Observable<BookingDto> {
    return this.http.get<BookingDto>(`${this.baseUrl}/bookings/${bookingId}`);
  }

  // Get user's bookings
  getUserBookings(personId: string, status?: string): Observable<BookingDto[]> {
    let params = new HttpParams();
    if (status) {
      params = params.set('status', status);
    }
    return this.http.get<BookingDto[]>(`${this.baseUrl}/bookings/user/${personId}`, { params });
  }

  // Create a booking
  createBooking(dto: { rideOfferId: string; pickupLocation: string; dropoffLocation: string; luggageCount: number; pet: boolean; kid: boolean; paymentMethod: PaymentMethod }): Observable<BookingDto> {
    return this.http.post<BookingDto>(`${this.baseUrl}/bookings`, dto);
  }

  // Cancel a booking
  cancelBooking(bookingId: string): Observable<BookingDto> {
    return this.http.put<BookingDto>(`${this.baseUrl}/bookings/${bookingId}/cancel`, {});
  }

  // Process payment
  processPayment(dto: { bookingId: string; paymentMethod: PaymentMethod; cardNumber?: string; cardExpiry?: string; cardCvv?: string; cardName?: string; paypalEmail?: string }): Observable<PaymentResultDto> {
    return this.http.post<PaymentResultDto>(`${this.baseUrl}/payments/process`, dto);
  }

  // Verify OTP for payment
  verifyPaymentOtp(bookingId: string, otp: string): Observable<PaymentResultDto> {
    return this.http.post<PaymentResultDto>(`${this.baseUrl}/payments/verify-otp`, { bookingId, otp });
  }

  // Chat messages
  getChatMessages(rideId: string): Observable<ChatMessage[]> {
    return this.http.get<ChatMessage[]>(`${this.baseUrl}/chat/ride/${rideId}`);
  }

  sendChatMessage(rideId: string, content: string): Observable<ChatMessage> {
    return this.http.post<ChatMessage>(`${this.baseUrl}/chat/ride/${rideId}`, { content });
  }

  markChatAsRead(rideId: string): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/chat/ride/${rideId}/read`, {});
  }

  // Reviews
  submitReview(review: { bookingId?: string; rideId: string; driverId: string; rating: number; comment: string; categories?: string[] }): Observable<ReviewDto> {
    return this.http.post<ReviewDto>(`${this.baseUrl}/reviews`, review);
  }

  getDriverReviews(driverId: string): Observable<ReviewDto[]> {
    return this.http.get<ReviewDto[]>(`${this.baseUrl}/reviews/driver/${driverId}`);
  }

  // Get driver profile
  getDriverProfile(driverId: string): Observable<DriverProfileDto> {
    return this.http.get<DriverProfileDto>(`${this.baseUrl}/drivers/${driverId}`);
  }

  // Get pending ride requests for driver
  getDriverRideRequests(driverId: string): Observable<RideRequest[]> {
    return this.http.get<RideRequest[]>(`${this.baseUrl}/persons/${driverId}/ride-requests`);
  }

  // Accept or reject ride request
  updateRideRequestStatus(requestId: string, status: 'ACCEPTED' | 'REJECTED'): Observable<RideRequest> {
    return this.http.put<RideRequest>(`${this.baseUrl}/riderequests/${requestId}/status`, { status });
  }

  // Create a ride offer (Post a Ride)
  createRideOffer(dto: CreateRideOfferDto): Observable<RideOfferSummaryDto> {
    return this.http.post<RideOfferSummaryDto>(`${this.baseUrl}/rideoffers`, dto);
  }

  // Get user's ride offers (as driver)
  getDriverRideOffers(driverId: string): Observable<RideOfferSummaryDto[]> {
    return this.http.get<RideOfferSummaryDto[]>(`${this.baseUrl}/rideoffers/driver/${driverId}`);
  }

  // Delete a ride offer
  deleteRideOffer(offerId: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/rideoffers/${offerId}`);
  }

  // Get user's cars
  getUserCars(personId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/persons/${personId}/cars`);
  }
}
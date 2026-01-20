# Share-A-Ride 🚗

A full-featured rideshare platform built with a **Java Spring Boot Backend**, **Angular Frontend**, and **Apache Derby Database**.

---

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Setup Instructions](#-setup-instructions)
  - [Backend Setup](#backend-setup)
  - [Frontend Setup](#frontend-setup)
- [Project Structure](#-project-structure)
- [Database Schema](#-database-schema)
- [Implemented Features](#-implemented-features)
- [API Endpoints](#-api-endpoints)
- [Future Enhancements](#-future-enhancements)

---

## ✨ Features

### Core Features Implemented by EAE Group 5
- **Find a Ride** - Search and browse available ride offers with filtering options (based on Balsamiq prototype)
- **Post a Ride** - Multi-step wizard for drivers to create ride offers (based on Balsamiq prototype)
- **Database Seeding** - SQL seed data for testing and demonstration

### Existing/Placeholder Pages
- Landing Page
- My Rides
- Booking Confirmation
- Payment
- Chat
- Feedback
- Driver Profile
- Reviews
- About Us / Contact Us / Support / Privacy Policy / Imprint

---

## 🛠 Tech Stack

| Layer | Technology |
|-------|------------|
| **Frontend** | Angular 17+ (Standalone Components), TypeScript, SCSS |
| **Backend** | Java 17+, Spring Boot 3.x, Maven, Spring Data JPA |
| **Database** | Apache Derby (Embedded) |
| **ORM** | Hibernate/JPA |

---

## 🚀 Setup Instructions

### Prerequisites
- Java 17+ installed
- Node.js (v18+) - Check with `node -v`
- Maven installed (or use `mvnw` wrapper)

### Backend Setup

1. Navigate to the backend folder:
   ```bash
   cd backend
   ```

2. Run the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```

   The embedded Derby database will automatically:
   - Create the database in `backend/data/sharearide`
   - Initialize tables based on JPA entities
   - Seed data from `src/main/resources/data.sql`

✅ Backend runs at `http://localhost:8080`

### Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd frontend/share-a-ride-ui
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the development server:
   ```bash
   npm start
   # or
   npx ng serve
   ```

✅ Frontend runs at `http://localhost:4200`

---

## 📁 Project Structure

```
rideshare/
├── backend/
│   ├── src/main/java/de/hnu/
│   │   ├── domain/           # JPA Entity models
│   │   │   ├── Booking.java
│   │   │   ├── Car.java
│   │   │   ├── ChatMessage.java
│   │   │   ├── Insurance.java
│   │   │   ├── Passenger.java
│   │   │   ├── Person.java
│   │   │   ├── Review.java
│   │   │   ├── Ride.java
│   │   │   ├── RideOffer.java
│   │   │   └── RideRequest.java
│   │   ├── repo/             # JPA repositories
│   │   ├── service/          # Business logic
│   │   └── web/              # REST controllers
│   │       └── dto/          # Data transfer objects
│   ├── src/main/resources/
│   │   ├── application.properties  # Derby DB config
│   │   └── data.sql               # SQL seed data
│   └── data/sharearide/      # Derby database files (auto-generated)
│
├── frontend/share-a-ride-ui/
│   └── src/app/
│       ├── core/api/         # API services
│       │   ├── person-api.ts
│       │   └── rides-api.ts
│       ├── layout/           # App layout with header/footer
│       ├── pages/
│       │   ├── booking-confirmation/
│       │   ├── chat/
│       │   ├── driver-profile/
│       │   ├── feedback/
│       │   ├── find-ride/
│       │   ├── landing/
│       │   ├── my-rides/
│       │   ├── payment/
│       │   ├── post-ride/
│       │   └── reviews/
│       └── shared/           # Reusable components
│           ├── header/
│           └── footer/
```

---

## 🗄 Database Schema

The application uses **Apache Derby** with the following relational schema:

### Entity Relationship Diagram

```
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│  INSURANCE  │───1:n─│     CAR     │───1:1─│   PERSON    │
└─────────────┘       └─────────────┘       └─────────────┘
                                                   │
                                                   │ 1:n (posts)
                                                   ▼
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│  PASSENGER  │───n:1─│    RIDE     │───n:1─│ RIDEOFFER   │
└─────────────┘       └─────────────┘       └─────────────┘
                             │                     │
                             │                     │ n:1 (gets)
                             ▼                     ▼
                      ┌─────────────┐       ┌─────────────┐
                      │   BOOKING   │       │ RIDEREQUEST │
                      └─────────────┘       └─────────────┘
```

### Tables

| Table | Description |
|-------|-------------|
| `insurance` | Car insurance policies |
| `car` | Vehicle information (FK: insurance_id) |
| `person` | Users - drivers and passengers (FK: car_id) |
| `rideoffer` | Posted ride offers (FK: driver_person_id, car_id) |
| `riderequest` | Booking requests from passengers |
| `ride` | Confirmed rides (FK: ride_offer_id, driver_person_id) |
| `passenger` | Passenger details for a ride (FK: ride_id, person_id) |
| `booking` | Booking records |
| `review` | User reviews and ratings |
| `chat_message` | Chat messages between users |

---

## ✅ Implemented Features

### Backend APIs

| Feature | Endpoint | Description |
|---------|----------|-------------|
| **Ride Offers** | `GET /api/ride-offers` | List available rides with filters |
| | `POST /api/ride-offers` | Create a new ride offer |
| | `GET /api/ride-offers/{id}` | Get ride offer details |
| **Bookings** | `GET /api/bookings/{id}` | Get booking details |
| | `GET /api/bookings/user/{userId}` | Get user's bookings |
| | `POST /api/bookings` | Create a new booking |
| | `POST /api/bookings/{id}/cancel` | Cancel a booking |
| **Payments** | `POST /api/payments/process` | Process payment |
| | `POST /api/payments/verify-otp` | Verify OTP for payment |
| **Reviews** | `GET /api/reviews/driver/{driverId}` | Get driver reviews |
| | `POST /api/reviews` | Submit a review |
| | `GET /api/reviews/driver/{id}/stats` | Get driver rating stats |
| **Chat** | `GET /api/chat/ride/{rideId}` | Get chat messages |
| | `POST /api/chat/ride/{rideId}` | Send a message |
| **Driver Profile** | `GET /api/driver-profile/{id}` | Get driver profile |
| **Cities** | `GET /api/cities` | Get available cities |

### Frontend Pages

| Page | Route | Status |
|------|-------|--------|
| Landing Page | `/` | 📄 Existing |
| **Find a Ride** | `/find` | ✅ **Implemented by Group 5** |
| **Post a Ride** | `/post-ride` | ✅ **Implemented by Group 5** (6-step wizard) |
| My Rides | `/my-rides` | 📄 Existing |
| Booking Confirmation | `/booking/:id` | 📄 Existing |
| Payment | `/payment` | 📄 Existing |
| Chat | `/chat/:rideId` | 📄 Existing |
| Feedback | `/feedback/:rideId` | 📄 Existing |
| Driver Profile | `/driver/:driverId` | 📄 Existing |
| Reviews | `/reviews` | 📄 Existing |
| About Us | `/about` | 📄 Existing |
| Contact Us | `/contact` | 📄 Existing |
| Support | `/support` | 📄 Existing |
| Privacy Policy | `/privacy` | 📄 Existing |
| Imprint | `/imprint` | 📄 Existing |

### Key Implementation Details (EAE Group 5)

#### Find a Ride
- Search rides by departure and destination cities
- Filter by date
- View available ride offers with driver info
- See pricing, seats available, and ride preferences
- Based on Balsamiq prototype design

#### Post a Ride (6-Step Wizard)
1. **Route** - Select departure and destination cities, add stops
2. **Schedule** - Pick date, time, and flexibility options
3. **Vehicle** - Choose car and set available seats/luggage
4. **Preferences** - Smoking, pets, music, chat level preferences
5. **Pricing** - Set price per person, accepted payment methods
6. **Review** - Confirm all details before publishing

#### Database Seeding
- Pre-populated Apache Derby with sample data via `data.sql`:
  - Users (drivers and passengers)
  - Cars with insurance details
  - Ride offers
  - Cities

---

## 🔌 API Endpoints

### Base URL
- Backend: `http://localhost:8080`
- Frontend: `http://localhost:4200`

### Sample API Calls

```bash
# Get available ride offers
curl http://localhost:8080/api/ride-offers

# Search rides by route
curl "http://localhost:8080/api/ride-offers?from=Ulm&to=Munich"

# Get driver profile
curl http://localhost:8080/api/driver-profile/person_driver_1

# Get driver reviews
curl http://localhost:8080/api/reviews/driver/person_driver_1
```

---

## 🚧 Future Enhancements

- [ ] **Authentication** - Login, Signup, Logout with JWT
- [ ] **User Profile Settings** - Edit profile, preferences
- [ ] **Booking System** - Complete booking flow with payment
- [ ] **Driver Dashboard** - Accept/reject ride requests
- [ ] **Real-time Chat** - Messaging between drivers and passengers
- [ ] **Reviews & Ratings** - Post-ride feedback system
- [ ] **Real-time Notifications** - Push notifications for booking updates
- [ ] **Email Integration** - Booking confirmations, ride reminders
- [ ] **AI Recommendations** - Personalized ride suggestions
- [ ] **Newsletter Subscription** - Marketing communications
- [ ] **Rent a Car** - Car rental feature
- [ ] **Offer Your Car** - Peer-to-peer car sharing

---

## 👥 Contributors

**EAE Group 5**

### What We Implemented:
- ✅ Find a Ride page (frontend + backend API)
- ✅ Post a Ride page with 6-step wizard (frontend + backend API)
- ✅ Apache Derby database seeding with sample data (`data.sql`)
- ✅ Based on Balsamiq prototype designs

---

## 📄 License

This project is for educational purposes as part of the EAE course.
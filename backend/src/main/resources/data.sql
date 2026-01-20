-- Share-A-Ride Database Seed Data for H2
-- This file runs automatically on application startup
-- Note: Using ddl-auto=create, tables are recreated on each startup

-- Insurance data
INSERT INTO insurance (id, name, policy_name, policy_number, coverage, passenger_driver_insurance) VALUES
(1, 'Allianz', 'Basis Schutz', 'AZ-123-456', 'Haftpflicht', TRUE),
(2, 'HUK-Coburg', 'Komfort', 'HUK-234-567', 'Teilkasko', TRUE),
(3, 'DEVK', 'Premium', 'DEVK-345-678', 'Vollkasko', TRUE);

-- Car data
INSERT INTO car (id, make, model, available_seats, luggage_space, smoking_allowed, pets_allowed, plate, build_year, insurance_id) VALUES
(1, 'Volkswagen', 'Golf', 4, 3, FALSE, TRUE, 'UL-AB-1234', 2019, 1),
(2, 'BMW', '320i', 3, 2, FALSE, FALSE, 'UL-CD-5678', 2021, 2),
(3, 'Mercedes-Benz', 'C200', 4, 4, TRUE, TRUE, 'NU-EF-9012', 2018, 3),
(4, 'Audi', 'A4', 4, 3, FALSE, TRUE, 'M-GH-3456', 2020, 1),
(5, 'Tesla', 'Model 3', 4, 2, FALSE, FALSE, 'S-IJ-7890', 2022, 2),
(6, 'Toyota', 'Corolla', 4, 3, FALSE, TRUE, 'A-KL-2345', 2020, 1),
(7, 'Ford', 'Focus', 4, 3, FALSE, FALSE, 'F-MN-6789', 2019, 2);

-- Person data (drivers and passengers)
INSERT INTO person (id, name, gender, age, profile_picture, home_city, bio, phone_number, email, car_id, languages, chatiness_level, overall_km_covered) VALUES
(1, 'Max Mustermann', 'male', 29, 'https://randomuser.me/api/portraits/men/1.jpg', 'Neu-Ulm', 'On-time driver, calm music, short breaks possible. I love road trips!', '+49 170 1234567', 'max@example.com', 1, 'DE,EN', 2, 42000),
(2, 'Anna Mueller', 'female', 25, 'https://randomuser.me/api/portraits/women/2.jpg', 'Ulm', 'Student at Uni Ulm. Regular commuter to Munich.', '+49 171 2345678', 'anna@example.com', NULL, 'DE,EN,FR', 3, 8500),
(3, 'Thomas Schmidt', 'male', 35, 'https://randomuser.me/api/portraits/men/3.jpg', 'Munich', 'Professional driver. Safety first!', '+49 172 3456789', 'thomas@example.com', 2, 'DE,EN', 1, 125000),
(4, 'Lisa Weber', 'female', 28, 'https://randomuser.me/api/portraits/women/4.jpg', 'Stuttgart', 'Eco-conscious driver. Electric car enthusiast.', '+49 173 4567890', 'lisa@example.com', 5, 'DE,EN,ES', 4, 35000),
(5, 'Klaus Fischer', 'male', 42, 'https://randomuser.me/api/portraits/men/5.jpg', 'Augsburg', 'Weekend driver. Love to chat about football!', '+49 174 5678901', 'klaus@example.com', 3, 'DE', 5, 67000),
(6, 'Sarah Braun', 'female', 31, 'https://randomuser.me/api/portraits/women/6.jpg', 'Ulm', 'Looking for rides to work.', '+49 175 6789012', 'sarah@example.com', NULL, 'DE,EN', 2, 5000),
(7, 'Michael Wagner', 'male', 27, 'https://randomuser.me/api/portraits/men/7.jpg', 'Neu-Ulm', 'Student. Flexible schedule.', '+49 176 7890123', 'michael@example.com', NULL, 'DE,EN,IT', 3, 3200),
(8, 'Julia Hoffmann', 'female', 33, 'https://randomuser.me/api/portraits/women/8.jpg', 'Augsburg', 'Regular commuter to Munich for work.', '+49 177 8901234', 'julia@example.com', 6, 'DE,EN', 3, 28000),
(9, 'Peter Becker', 'male', 45, 'https://randomuser.me/api/portraits/men/9.jpg', 'Frankfurt', 'Business traveler. Comfortable rides.', '+49 178 9012345', 'peter@example.com', 7, 'DE,EN,FR', 2, 95000);

-- RideOffer data with varied dates and routes
INSERT INTO rideoffer (id, departure_city, destination_city, departure_time, seats_available, price_per_person, luggage_count, driver_person_id, car_id, smoking_allowed, pets_allowed, music_allowed, chat_level, additional_notes, flexible_time, flexibility_minutes, stops, accepted_payment_methods) VALUES
-- Today's rides
(1, 'Ulm', 'Munich', '2026-01-20 14:00:00', 3, 15.50, 2, 1, 1, FALSE, TRUE, TRUE, 2, 'Direct route via A8. Coffee stop possible.', TRUE, 15, 'Augsburg', 'CASH,CARD'),
(2, 'Munich', 'Ulm', '2026-01-20 17:30:00', 2, 14.00, 2, 3, 2, FALSE, FALSE, TRUE, 1, 'Evening commute. Quiet ride.', FALSE, 0, NULL, 'CARD'),
(3, 'Stuttgart', 'Ulm', '2026-01-20 18:00:00', 3, 12.00, 3, 4, 5, FALSE, FALSE, FALSE, 4, 'Electric car. Eco-friendly!', TRUE, 30, NULL, 'CARD,PAYPAL'),

-- Tomorrow's rides
(4, 'Ulm', 'Stuttgart', '2026-01-21 07:00:00', 4, 18.00, 3, 1, 1, FALSE, TRUE, TRUE, 2, 'Early morning commute.', TRUE, 15, NULL, 'CASH,CARD'),
(5, 'Augsburg', 'Munich', '2026-01-21 08:30:00', 3, 8.00, 2, 8, 6, FALSE, TRUE, TRUE, 3, 'Regular morning commute.', FALSE, 0, NULL, 'CASH,CARD'),
(6, 'Munich', 'Stuttgart', '2026-01-21 09:00:00', 2, 25.00, 4, 3, 2, FALSE, FALSE, TRUE, 1, 'Business trip.', FALSE, 0, 'Ulm', 'CARD,PAYPAL'),
(7, 'Neu-Ulm', 'Munich', '2026-01-21 10:00:00', 3, 16.00, 2, 5, 3, TRUE, TRUE, TRUE, 5, 'Happy to chat! Pets welcome.', TRUE, 30, 'Augsburg', 'CASH,CARD,PAYPAL'),

-- Day after tomorrow
(8, 'Ulm', 'Augsburg', '2026-01-22 08:00:00', 4, 10.00, 3, 1, 1, FALSE, TRUE, TRUE, 2, 'Short morning trip.', TRUE, 20, NULL, 'CASH'),
(9, 'Stuttgart', 'Munich', '2026-01-22 11:00:00', 3, 28.00, 3, 4, 5, FALSE, FALSE, FALSE, 4, 'Midday ride.', TRUE, 15, 'Ulm,Augsburg', 'CARD'),
(10, 'Munich', 'Neu-Ulm', '2026-01-22 16:00:00', 4, 15.00, 4, 5, 3, TRUE, TRUE, TRUE, 5, 'Afternoon return.', TRUE, 30, 'Augsburg', 'CASH,CARD,PAYPAL'),

-- Weekend rides
(11, 'Ulm', 'Munich', '2026-01-25 08:00:00', 3, 15.50, 2, 1, 1, FALSE, TRUE, TRUE, 2, 'Weekend trip. Relaxed schedule.', TRUE, 30, 'Augsburg', 'CASH,CARD'),
(12, 'Munich', 'Ulm', '2026-01-25 18:00:00', 4, 14.00, 4, 5, 3, TRUE, TRUE, TRUE, 5, 'Weekend return.', TRUE, 30, 'Augsburg,Guenzburg', 'CASH,CARD,PAYPAL'),
(13, 'Stuttgart', 'Munich', '2026-01-26 09:00:00', 3, 26.00, 2, 4, 5, FALSE, FALSE, FALSE, 4, 'Sunday trip.', TRUE, 15, 'Ulm', 'CARD'),
(14, 'Augsburg', 'Stuttgart', '2026-01-26 10:30:00', 3, 20.00, 3, 8, 6, FALSE, TRUE, TRUE, 3, 'Day trip.', TRUE, 20, 'Ulm', 'CASH,CARD'),

-- Next week rides
(15, 'Ulm', 'Frankfurt', '2026-01-27 06:00:00', 2, 35.00, 2, 1, 1, FALSE, TRUE, TRUE, 2, 'Long distance. Multiple stops available.', TRUE, 15, 'Stuttgart,Heidelberg', 'CASH,CARD'),
(16, 'Frankfurt', 'Munich', '2026-01-28 14:00:00', 3, 40.00, 4, 9, 7, FALSE, FALSE, TRUE, 2, 'Comfortable business trip.', FALSE, 0, 'Stuttgart,Ulm', 'CARD,PAYPAL'),
(17, 'Munich', 'Frankfurt', '2026-01-29 07:30:00', 4, 38.00, 3, 3, 2, FALSE, FALSE, TRUE, 1, 'Early departure.', TRUE, 30, 'Augsburg,Stuttgart', 'CARD'),
(18, 'Neu-Ulm', 'Stuttgart', '2026-01-30 09:00:00', 3, 17.00, 3, 5, 3, TRUE, TRUE, TRUE, 5, 'Morning ride. Great conversation!', TRUE, 15, NULL, 'CASH,CARD,PAYPAL');

-- Ride data (confirmed rides from offers)
INSERT INTO ride (id, ride_offer_id, departure_city, destination_city, departure_time, driver_person_id, seats_remaining, luggage_remaining, status) VALUES
(1, 11, 'Ulm', 'Munich', '2026-01-25 08:00:00', 1, 2, 1, 'ACTIVE'),
(2, 12, 'Munich', 'Ulm', '2026-01-25 18:00:00', 5, 3, 3, 'ACTIVE');

-- RideRequest data
INSERT INTO riderequest (id, ride_offer_id, person_id, pickup_location, dropoff_location, timestamp, luggage_count, pet, kid, payment_method, status, ride_id, passenger_id) VALUES
(1, 11, 2, 'Ulm Hauptbahnhof', 'Munich Central', '2026-01-20 10:00:00', 1, FALSE, FALSE, 'CARD', 'ACCEPTED', 1, 1),
(2, 12, 6, 'Munich Ost', 'Ulm Hauptbahnhof', '2026-01-20 14:00:00', 1, FALSE, FALSE, 'CASH', 'ACCEPTED', 2, 2),
(3, 4, 7, 'Neu-Ulm Station', 'Stuttgart Main', '2026-01-20 09:00:00', 2, FALSE, FALSE, 'PAYPAL', 'PENDING', NULL, NULL);

-- Passenger data
INSERT INTO passenger (id, ride_id, person_id, luggage_count, payment_method, payment_outstanding, pickup_location, dropoff_location, pet, kid, seats_consumed) VALUES
(1, 1, 2, 1, 'CARD', FALSE, 'Ulm Hauptbahnhof', 'Munich Central', FALSE, FALSE, 1),
(2, 2, 6, 1, 'CASH', TRUE, 'Munich Ost', 'Ulm Hauptbahnhof', FALSE, FALSE, 1);

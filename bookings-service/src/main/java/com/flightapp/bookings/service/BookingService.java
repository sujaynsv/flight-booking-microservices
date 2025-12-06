package com.flightapp.bookings.service;

import com.flightapp.bookings.client.FlightsClient;
import com.flightapp.bookings.dto.BookingRequest;
import com.flightapp.bookings.dto.EmailNotification;
import com.flightapp.bookings.dto.FlightDTO;
import com.flightapp.bookings.exception.BookingNotFoundException;
import com.flightapp.bookings.exception.CancellationNotAllowedException;
import com.flightapp.bookings.model.Booking;
import com.flightapp.bookings.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class BookingService {
    
    private static final Logger log = LoggerFactory.getLogger(BookingService.class);
    private static final ZoneId FLIGHT_TIMEZONE = ZoneId.of("Asia/Kolkata");
    
    private final BookingRepository bookingRepository;
    private final FlightsClient flightsClient;
    private final EmailService emailService;
    private final ReactiveCircuitBreaker circuitBreaker;
    
    public BookingService(BookingRepository bookingRepository, 
                         FlightsClient flightsClient, 
                         EmailService emailService,
                         ReactiveCircuitBreakerFactory circuitBreakerFactory) {
        this.bookingRepository = bookingRepository;
        this.flightsClient = flightsClient;
        this.emailService = emailService;
        this.circuitBreaker = circuitBreakerFactory.create("flightsService");
    }
    
    @CacheEvict(value = {"bookings", "bookingHistory"}, allEntries = true)
    public Mono<Booking> createBooking(String flightId, BookingRequest request) {
        log.info("Creating booking for flight: {}", flightId);
        
        return circuitBreaker.run(
            flightsClient.getFlightById(flightId)
                .flatMap(flight -> {
                    return flightsClient.updateSeats(flightId, request.getNumberOfSeats())
                            .flatMap(updatedFlight -> {
                                Booking booking = new Booking();
                                booking.setPnr(generatePNR());
                                booking.setFlightId(flightId);
                                booking.setEmail(request.getEmail());
                                booking.setName(request.getName());
                                booking.setNumberOfSeats(request.getNumberOfSeats());
                                booking.setPassengers(request.getPassengers());
                                booking.setSeatNumbers(request.getSeatNumbers());
                                booking.setTotalPrice(flight.getPrice() * request.getNumberOfSeats());
                                booking.setStatus(Booking.BookingStatus.CONFIRMED);
                                booking.setBookingDateTime(LocalDateTime.now());
                                booking.setJourneyDate(flight.getDepartureDateTime());
                                
                                return bookingRepository.save(booking)
                                        .doOnSuccess(b -> {
                                            log.info("Booking created successfully with PNR: {}", b.getPnr());
                                            sendBookingEmail(b, flight).subscribe();
                                        });
                            });
                }),
            throwable -> createBookingFallback(flightId, request, throwable)
        );
    }
    
    private Mono<Booking> createBookingFallback(String flightId, BookingRequest request, Throwable ex) {
        log.error("Circuit breaker triggered for flight: {}", flightId);
        return Mono.error(new RuntimeException("Circuit breaker - Unable to complete booking. Please try again later."));
    }
    
    @Cacheable(value = "bookings", key = "#pnr")
    public Mono<Booking> getBookingByPnr(String pnr) {
        log.info("CACHE MISS - Fetching booking from MongoDB: {}", pnr);
        return bookingRepository.findByPnr(pnr)
                .switchIfEmpty(Mono.error(new BookingNotFoundException("Booking not found with PNR: " + pnr)))
                .doOnSuccess(b -> log.info("Booking fetched and cached: {}", pnr));
    }
    
    @Cacheable(value = "bookingHistory", key = "#email")
    public Flux<Booking> getBookingHistory(String email) {
        log.info("CACHE MISS - Fetching booking history from MongoDB for: {}", email);
        return bookingRepository.findByEmail(email)
                .doOnComplete(() -> log.info("Booking history fetched and cached for: {}", email));
    }
    
    @CacheEvict(value = {"bookings", "bookingHistory"}, allEntries = true)
    public Mono<Void> cancelBooking(String pnr) {
        log.info("Cancelling booking and clearing cache: {}", pnr);
        
        return bookingRepository.findByPnr(pnr)
                .switchIfEmpty(Mono.error(new BookingNotFoundException("Booking not found with PNR: " + pnr)))
                .flatMap(booking -> {
                    ZonedDateTime journeyDateTime = booking.getJourneyDate()
                            .atZone(FLIGHT_TIMEZONE);
                    
                    ZonedDateTime now = ZonedDateTime.now(FLIGHT_TIMEZONE);
                    Duration duration = Duration.between(now, journeyDateTime);
                    
                    log.info("Current time ({}): {}", FLIGHT_TIMEZONE, now);
                    log.info("Journey time: {}", journeyDateTime);
                    log.info("Hours until journey: {}", duration.toHours());
                    
                    if (duration.toHours() < 24) {
                        return Mono.error(new CancellationNotAllowedException(
                                "Cancellation not allowed within 24 hours of journey date"));
                    }
                   
                    booking.setStatus(Booking.BookingStatus.CANCELLED);
                    return bookingRepository.save(booking)
                            .doOnSuccess(b -> log.info("Booking cancelled and cache cleared: {}", pnr))
                            .then();
                });
    }
    
    private Mono<Void> sendBookingEmail(Booking booking, FlightDTO flight) {
        EmailNotification notification = new EmailNotification();
        notification.setTo(booking.getEmail());
        notification.setSubject("Flight Booking Confirmation - PNR: " + booking.getPnr());
        notification.setBody(buildEmailBody(booking, flight));
        
        return emailService.sendEmail(notification);
    }
    
    private String buildEmailBody(Booking booking, FlightDTO flight) {
        return String.format("""
                Dear %s,
                
                Your flight booking has been confirmed!
                
                Booking Details:
                ================
                PNR: %s
                Flight: %s
                From: %s
                To: %s
                Departure: %s
                Number of Seats: %d
                Seat Numbers: %s
                Total Price: Rs. %.2f
                
                Thank you for booking with us!
                
                Best Regards,
                Flight Booking Team
                """,
                booking.getName(),
                booking.getPnr(),
                flight.getAirlineName(),
                flight.getFromPlace(),
                flight.getToPlace(),
                flight.getDepartureDateTime(),
                booking.getNumberOfSeats(),
                String.join(", ", booking.getSeatNumbers()),
                booking.getTotalPrice()
        );
    }
    
    private String generatePNR() {
        return "PNR" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

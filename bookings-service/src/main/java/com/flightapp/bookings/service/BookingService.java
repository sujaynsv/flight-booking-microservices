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
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class BookingService {
    
    private static final Logger log = LoggerFactory.getLogger(BookingService.class);
    
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
    
    public Mono<Booking> createBooking(String flightId, BookingRequest request) {
        log.info("Creating booking for flight: {}", flightId);
        
        return circuitBreaker.run(
            // Main logic
            flightsClient.getFlightById(flightId)
                .flatMap(flight -> {
                    // Update flight seats
                    return flightsClient.updateSeats(flightId, request.getNumberOfSeats())
                            .flatMap(updatedFlight -> {
                                // Create booking
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
                                booking.setJourneyDate(flight.getDepartureDateTime().toLocalDate().atStartOfDay());
                                
                                return bookingRepository.save(booking)
                                        .doOnSuccess(b -> {
                                            log.info("Booking created successfully with PNR: {}", b.getPnr());
                                            // Send email notification asynchronously
                                            sendBookingEmail(b, flight).subscribe();
                                        });
                            });
                }),
            // Fallback
            throwable -> createBookingFallback(flightId, request, throwable)
        );
    }
    
    // FALLBACK METHOD - Called when circuit breaker opens
    private Mono<Booking> createBookingFallback(String flightId, BookingRequest request, Throwable ex) {
    			log.error("circuit breaker trigerred");
        
        return Mono.error(new RuntimeException(
        		"Circuit breaker"
        ));
    }
    
    public Mono<Booking> getBookingByPnr(String pnr) {
        return bookingRepository.findByPnr(pnr)
                .switchIfEmpty(Mono.error(new BookingNotFoundException("Booking not found with PNR: " + pnr)));
    }
    
    public Flux<Booking> getBookingHistory(String email) {
        return bookingRepository.findByEmail(email);
    }
    
    public Mono<Void> cancelBooking(String pnr) {
        return bookingRepository.findByPnr(pnr)
                .switchIfEmpty(Mono.error(new BookingNotFoundException("Booking not found with PNR: " + pnr)))
                .flatMap(booking -> {
                    LocalDateTime now = LocalDateTime.now();
                    Duration duration = Duration.between(now, booking.getJourneyDate());
                    
                    if (duration.toHours() < 24) {
                        return Mono.error(new CancellationNotAllowedException(
                                "Cancellation not allowed within 24 hours of journey date"));
                    }
                    
                    booking.setStatus(Booking.BookingStatus.CANCELLED);
                    return bookingRepository.save(booking)
                            .doOnSuccess(b -> log.info("Booking cancelled successfully: {}", pnr))
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
                Total Price: ₹%.2f
                
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

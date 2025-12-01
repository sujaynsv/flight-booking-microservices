package com.flightapp.bookings.service;

import com.flightapp.bookings.client.FlightsClient;
import com.flightapp.bookings.exception.BookingNotFoundException;
import com.flightapp.bookings.exception.CancellationNotAllowedException;
import com.flightapp.bookings.model.Booking;
import com.flightapp.bookings.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FlightsClient flightsClient; // only for constructor

    @Mock
    private EmailService emailService;   // only for constructor

    @Mock
    private ReactiveCircuitBreakerFactory circuitBreakerFactory; // only for constructor

    @InjectMocks
    private BookingService bookingService;

    private Booking booking;

    @BeforeEach
    void setUp() {
        booking = new Booking();
        booking.setId("1");
        booking.setPnr("PNR123");
        booking.setFlightId("FL1");
        booking.setEmail("test@example.com");
        booking.setName("Test User");
        booking.setNumberOfSeats(2);
        booking.setSeatNumbers(List.of("1A", "1B"));
        booking.setJourneyDate(LocalDateTime.now().plusDays(2));
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        booking.setTotalPrice(10000.0);
    }

    @Test
    void testGetBookingByPnrSuccess() {
        when(bookingRepository.findByPnr("PNR123")).thenReturn(Mono.just(booking));

        StepVerifier.create(bookingService.getBookingByPnr("PNR123"))
                .assertNext(b -> {
                    assertEquals("PNR123", b.getPnr());
                    assertEquals("test@example.com", b.getEmail());
                })
                .verifyComplete();

        verify(bookingRepository, times(1)).findByPnr("PNR123");
    }

    @Test
    void testGetBookingByPnrNotFound() {
        when(bookingRepository.findByPnr("PNR999")).thenReturn(Mono.empty());

        StepVerifier.create(bookingService.getBookingByPnr("PNR999"))
                .expectError(BookingNotFoundException.class)
                .verify();

        verify(bookingRepository, times(1)).findByPnr("PNR999");
    }

    @Test
    void testGetBookingHistoryNonEmpty() {
        when(bookingRepository.findByEmail("test@example.com"))
                .thenReturn(Flux.just(booking));

        StepVerifier.create(bookingService.getBookingHistory("test@example.com"))
                .expectNext(booking)
                .verifyComplete();

        verify(bookingRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testGetBookingHistoryEmpty() {
        when(bookingRepository.findByEmail("other@example.com"))
                .thenReturn(Flux.empty());

        StepVerifier.create(bookingService.getBookingHistory("other@example.com"))
                .verifyComplete();

        verify(bookingRepository, times(1)).findByEmail("other@example.com");
    }

    @Test
    void testCancelBookingSuccess() {
        Booking futureBooking = new Booking();
        futureBooking.setPnr("PNR123");
        futureBooking.setJourneyDate(LocalDateTime.now().plusDays(2));
        futureBooking.setStatus(Booking.BookingStatus.CONFIRMED);

        when(bookingRepository.findByPnr("PNR123")).thenReturn(Mono.just(futureBooking));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0, Booking.class)));

        StepVerifier.create(bookingService.cancelBooking("PNR123"))
                .verifyComplete();

        verify(bookingRepository, times(1)).findByPnr("PNR123");
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testCancelBookingTooLate() {
        Booking nearJourney = new Booking();
        nearJourney.setPnr("PNR123");
        nearJourney.setJourneyDate(LocalDateTime.now().plusHours(10));

        when(bookingRepository.findByPnr("PNR123")).thenReturn(Mono.just(nearJourney));

        StepVerifier.create(bookingService.cancelBooking("PNR123"))
                .expectError(CancellationNotAllowedException.class)
                .verify();

        verify(bookingRepository, times(1)).findByPnr("PNR123");
    }

    @Test
    void testCancelBookingNotFound() {
        when(bookingRepository.findByPnr("PNR999")).thenReturn(Mono.empty());

        StepVerifier.create(bookingService.cancelBooking("PNR999"))
                .expectError(BookingNotFoundException.class)
                .verify();

        verify(bookingRepository, times(1)).findByPnr("PNR999");
    }
}

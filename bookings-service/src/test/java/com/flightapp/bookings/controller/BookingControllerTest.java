package com.flightapp.bookings.controller;

import com.flightapp.bookings.dto.BookingRequest;
import com.flightapp.bookings.model.Booking;
import com.flightapp.bookings.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private Booking booking;
    private BookingRequest request;

    @BeforeEach
    void setUp() {
        booking = new Booking();
        booking.setPnr("PNR123");
        booking.setEmail("test@example.com");

        request = new BookingRequest();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setNumberOfSeats(2);
    }

    @Test
    void testBookTicket() {
        when(bookingService.createBooking(anyString(), any(BookingRequest.class)))
                .thenReturn(Mono.just(booking));

        StepVerifier.create(bookingController.bookTicket("flight1", request))
                .assertNext(response -> {
                    assertEquals(200, response.getStatusCodeValue());
                    Map<String, String> body = response.getBody();
                    assertNotNull(body);
                    assertEquals("PNR123", body.get("pnr"));
                })
                .verifyComplete();

        verify(bookingService, times(1)).createBooking(eq("flight1"), any(BookingRequest.class));
    }

    @Test
    void testGetTicketByPnr() {
        when(bookingService.getBookingByPnr("PNR123")).thenReturn(Mono.just(booking));

        StepVerifier.create(bookingController.getTicketByPnr("PNR123"))
                .expectNext(booking)
                .verifyComplete();

        verify(bookingService, times(1)).getBookingByPnr("PNR123");
    }

    @Test
    void testGetBookingHistory() {
        when(bookingService.getBookingHistory("test@example.com"))
                .thenReturn(Flux.just(booking));

        StepVerifier.create(bookingController.getBookingHistory("test@example.com"))
                .expectNext(booking)
                .verifyComplete();

        verify(bookingService, times(1)).getBookingHistory("test@example.com");
    }

    @Test
    void testCancelBooking() {
        when(bookingService.cancelBooking("PNR123")).thenReturn(Mono.empty());

        StepVerifier.create(bookingController.cancelBooking("PNR123"))
                .assertNext(ResponseEntity::ok)
                .verifyComplete();

        verify(bookingService, times(1)).cancelBooking("PNR123");
    }
}

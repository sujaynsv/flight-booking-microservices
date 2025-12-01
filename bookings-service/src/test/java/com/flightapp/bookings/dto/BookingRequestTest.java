package com.flightapp.bookings.dto;

import com.flightapp.bookings.model.Passenger;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingRequestTest {

    @Test
    void testGettersAndSetters() {
        BookingRequest request = new BookingRequest();

        Passenger p = new Passenger();
        p.setName("Passenger 1");

        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setNumberOfSeats(2);
        request.setPassengers(List.of(p));
        request.setSeatNumbers(List.of("1A", "1B"));

        assertEquals("Test User", request.getName());
        assertEquals("test@example.com", request.getEmail());
        assertEquals(2, request.getNumberOfSeats());
        assertEquals(1, request.getPassengers().size());
        assertEquals("1A", request.getSeatNumbers().get(0));
    }

    @Test
    void testConstructor() {
        Passenger p = new Passenger("A", "M", 30, "VEG");

        BookingRequest request = new BookingRequest(
                "Test User",
                "test@example.com",
                2,
                List.of(p),
                List.of("1A", "1B")
        );

        assertEquals("Test User", request.getName());
        assertEquals("test@example.com", request.getEmail());
        assertEquals(2, request.getNumberOfSeats());
        assertEquals(1, request.getPassengers().size());
        assertEquals("1A", request.getSeatNumbers().get(0));
    }
}

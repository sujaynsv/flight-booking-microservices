package com.flightapp.bookings.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    @Test
    void testAllFields() {
        Booking booking = new Booking();
        LocalDateTime now = LocalDateTime.now();

        booking.setId("1");
        booking.setPnr("PNR123");
        booking.setFlightId("FL1");
        booking.setEmail("test@example.com");
        booking.setName("Test User");
        booking.setNumberOfSeats(2);
        booking.setPassengers(List.of(new Passenger("A", "M", 30, "VEG")));
        booking.setSeatNumbers(List.of("1A", "1B"));
        booking.setTotalPrice(10000.0);
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        booking.setBookingDateTime(now);
        booking.setJourneyDate(now.plusDays(1));

        assertEquals("1", booking.getId());
        assertEquals("PNR123", booking.getPnr());
        assertEquals("FL1", booking.getFlightId());
        assertEquals("test@example.com", booking.getEmail());
        assertEquals("Test User", booking.getName());
        assertEquals(2, booking.getNumberOfSeats());
        assertEquals(1, booking.getPassengers().size());
        assertEquals("1A", booking.getSeatNumbers().get(0));
        assertEquals(10000.0, booking.getTotalPrice());
        assertEquals(Booking.BookingStatus.CONFIRMED, booking.getStatus());
        assertEquals(now, booking.getBookingDateTime());
        assertEquals(now.plusDays(1), booking.getJourneyDate());
    }

    @Test
    void testDefaultConstructor() {
        Booking booking = new Booking();
        assertNotNull(booking);
    }
}

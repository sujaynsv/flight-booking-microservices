package com.flightapp.flights.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FlightTest {

    @Test
    void testFlightAllFields() {
        Flight flight = new Flight();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime later = now.plusHours(2);

        flight.setId("1");
        flight.setFlightNumber("6E123");
        flight.setAirlineId("airline123");
        flight.setFromPlace("Delhi");
        flight.setToPlace("Mumbai");
        flight.setDepartureDateTime(now);
        flight.setArrivalDateTime(later);

        flight.setTotalSeats(180);
        flight.setAvailableSeats(100);
        flight.setPrice(5000.0);
        flight.setCreatedAt(now);
        flight.setUpdatedAt(later);

        assertEquals("1", flight.getId());
        assertEquals("6E123", flight.getFlightNumber());
        assertEquals("airline123", flight.getAirlineId());
        assertEquals("Delhi", flight.getFromPlace());
        assertEquals("Mumbai", flight.getToPlace());
        assertEquals(now, flight.getDepartureDateTime());
        assertEquals(later, flight.getArrivalDateTime());

        assertEquals(180, flight.getTotalSeats());
        assertEquals(100, flight.getAvailableSeats());
        assertEquals(5000.0, flight.getPrice());
        assertEquals(now, flight.getCreatedAt());
        assertEquals(later, flight.getUpdatedAt());
    }

    @Test
    void testFlightDefaultConstructor() {
        Flight flight = new Flight();
        assertNotNull(flight);
    }
}

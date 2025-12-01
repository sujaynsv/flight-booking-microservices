package com.flightapp.flights.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomExceptionsTest {

    @Test
    void testInsufficientSeatsExceptionMessage() {
        InsufficientSeatsException ex = new InsufficientSeatsException("Not enough seats");
        assertEquals("Not enough seats", ex.getMessage());
    }

    @Test
    void testFlightNotFoundExceptionMessage() {
        FlightNotFoundException ex = new FlightNotFoundException("Flight not found");
        assertEquals("Flight not found", ex.getMessage());
    }
}

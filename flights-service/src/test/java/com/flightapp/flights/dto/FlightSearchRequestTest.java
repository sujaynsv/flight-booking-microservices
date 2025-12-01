package com.flightapp.flights.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FlightSearchRequestTest {

    @Test
    void testGettersAndSetters() {
        FlightSearchRequest request = new FlightSearchRequest();

        LocalDate date = LocalDate.now();

        request.setFromPlace("Delhi");
        request.setToPlace("Mumbai");
        request.setDepartureDate(date);

        assertEquals("Delhi", request.getFromPlace());
        assertEquals("Mumbai", request.getToPlace());
        assertEquals(date, request.getDepartureDate());
    }

    @Test
    void testDefaultState() {
        FlightSearchRequest request = new FlightSearchRequest();
        assertNull(request.getFromPlace());
        assertNull(request.getToPlace());
        assertNull(request.getDepartureDate());
    }

    @Test
    void testToStringNotNull() {
        FlightSearchRequest request = new FlightSearchRequest();
        request.setFromPlace("Delhi");
        request.setToPlace("Mumbai");
        request.setDepartureDate(LocalDate.now());

        assertNotNull(request.toString());
    }
}

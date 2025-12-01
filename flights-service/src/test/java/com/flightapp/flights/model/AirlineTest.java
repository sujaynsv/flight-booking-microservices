package com.flightapp.flights.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AirlineTest {

    @Test
    void testAirlineAllFields() {
        Airline airline = new Airline();

        airline.setId("airline123");
        airline.setAirlineName("IndiGo");
        airline.setAirlineCode("6E");


        assertEquals("airline123", airline.getId());
        assertEquals("IndiGo", airline.getAirlineName());
        assertEquals("6E", airline.getAirlineCode());

    }

    @Test
    void testAirlineDefaultConstructor() {
        Airline airline = new Airline();
        assertNotNull(airline);
    }
}

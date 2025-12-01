package com.flightapp.bookings.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FlightDTOTest {

    @Test
    void testAllFields() {
        FlightDTO dto = new FlightDTO();
        LocalDateTime dep = LocalDateTime.now();
        LocalDateTime arr = dep.plusHours(2);

        dto.setId("FL1");
        dto.setAirlineId("AIR1");
        dto.setAirlineName("TestAir");
        dto.setFlightNumber("TA123");
        dto.setFromPlace("A");
        dto.setToPlace("B");
        dto.setTotalSeats(180);
        dto.setAvailableSeats(150);
        dto.setTripType("ONE_WAY");
        dto.setDepartureDateTime(dep);
        dto.setArrivalDateTime(arr);
        dto.setPrice(5000.0);

        assertEquals("FL1", dto.getId());
        assertEquals("AIR1", dto.getAirlineId());
        assertEquals("TestAir", dto.getAirlineName());
        assertEquals("TA123", dto.getFlightNumber());
        assertEquals("A", dto.getFromPlace());
        assertEquals("B", dto.getToPlace());
        assertEquals(180, dto.getTotalSeats());
        assertEquals(150, dto.getAvailableSeats());
        assertEquals("ONE_WAY", dto.getTripType());
        assertEquals(dep, dto.getDepartureDateTime());
        assertEquals(arr, dto.getArrivalDateTime());
        assertEquals(5000.0, dto.getPrice());
    }

    @Test
    void testDefaultConstructor() {
        FlightDTO dto = new FlightDTO();
        assertNotNull(dto);
    }
}

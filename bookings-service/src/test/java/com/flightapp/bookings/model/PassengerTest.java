package com.flightapp.bookings.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PassengerTest {

    @Test
    void testAllFields() {
        Passenger p = new Passenger();
        p.setName("Passenger 1");
        p.setGender("M");
        p.setAge(30);
        p.setMealPreference("VEG");

        assertEquals("Passenger 1", p.getName());
        assertEquals("M", p.getGender());
        assertEquals(30, p.getAge());
        assertEquals("VEG", p.getMealPreference());
    }

    @Test
    void testConstructor() {
        Passenger p = new Passenger("A", "F", 25, "NON_VEG");
        assertEquals("A", p.getName());
        assertEquals("F", p.getGender());
        assertEquals(25, p.getAge());
        assertEquals("NON_VEG", p.getMealPreference());
    }
}

package com.flightapp.flights.service;

import com.flightapp.flights.dto.FlightSearchRequest;
import com.flightapp.flights.model.Flight;
import com.flightapp.flights.repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private FlightService flightService;

    private Flight testFlight;

    @BeforeEach
    void setUp() {
        testFlight = new Flight();
        testFlight.setId("1");
        testFlight.setFlightNumber("6E123");
        testFlight.setFromPlace("Delhi");
        testFlight.setToPlace("Mumbai");
        testFlight.setTotalSeats(180);
        testFlight.setAvailableSeats(100);
        testFlight.setPrice(5000.0);
    }

    @Test
    void testAddFlight() {
        when(flightRepository.save(any(Flight.class))).thenReturn(Mono.just(testFlight));

        StepVerifier.create(flightService.addFlight(testFlight))
                .expectNext(testFlight)
                .verifyComplete();

        verify(flightRepository, times(1)).save(any(Flight.class));
    }

    @Test
    void testAddFlightSetsTimestampsAndSeats() {
        when(flightRepository.save(any(Flight.class)))
                .thenAnswer(invocation -> {
                    Flight f = invocation.getArgument(0, Flight.class);
                    return Mono.just(f);
                });

        Flight flight = new Flight();
        flight.setTotalSeats(180);

        StepVerifier.create(flightService.addFlight(flight))
                .assertNext(saved -> {
                    assertNotNull(saved.getCreatedAt());
                    assertNotNull(saved.getUpdatedAt());
                    assertEquals(180, saved.getTotalSeats());
                    assertEquals(180, saved.getAvailableSeats());
                })
                .verifyComplete();

        verify(flightRepository, times(1)).save(any(Flight.class));
    }

    @Test
    void testGetFlightById() {
        when(flightRepository.findById("1")).thenReturn(Mono.just(testFlight));

        StepVerifier.create(flightService.getFlightById("1"))
                .expectNext(testFlight)
                .verifyComplete();

        verify(flightRepository, times(1)).findById("1");
    }

    @Test
    void testGetFlightById_NotFound() {
        when(flightRepository.findById("999")).thenReturn(Mono.empty());

        StepVerifier.create(flightService.getFlightById("999"))
                .expectErrorMatches(ex ->
                        ex instanceof RuntimeException &&
                        ex.getMessage().contains("Flight not found with ID: 999"))
                .verify();
    }

    @Test
    void testGetAllFlights() {
        when(flightRepository.findAll()).thenReturn(Flux.just(testFlight));

        StepVerifier.create(flightService.getAllFlights())
                .expectNext(testFlight)
                .verifyComplete();

        verify(flightRepository, times(1)).findAll();
    }

    @Test
    void testUpdateSeats_Success() {
        when(flightRepository.findById("1")).thenReturn(Mono.just(testFlight));
        when(flightRepository.save(any(Flight.class)))
                .thenAnswer(invocation -> {
                    Flight f = invocation.getArgument(0, Flight.class);
                    return Mono.just(f);
                });

        StepVerifier.create(flightService.updateSeats("1", 10))
                .assertNext(flight -> {
                    assertEquals(90, flight.getAvailableSeats());
                    assertNotNull(flight.getUpdatedAt());
                })
                .verifyComplete();

        verify(flightRepository, times(1)).findById("1");
        verify(flightRepository, times(1)).save(any(Flight.class));
    }

    @Test
    void testUpdateSeats_NotEnoughSeats() {
        when(flightRepository.findById("1")).thenReturn(Mono.just(testFlight));

        StepVerifier.create(flightService.updateSeats("1", 150))
                .expectErrorMatches(ex ->
                        ex instanceof RuntimeException &&
                        ex.getMessage().contains("Not enough seats available"))
                .verify();
    }

    @Test
    void testUpdateSeats_FlightNotFound() {
        when(flightRepository.findById("999")).thenReturn(Mono.empty());

        StepVerifier.create(flightService.updateSeats("999", 10))
                .expectErrorMatches(ex ->
                        ex instanceof RuntimeException &&
                        ex.getMessage().contains("Flight not found with ID: 999"))
                .verify();
    }

    @Test
    void testDeleteFlight() {
        when(flightRepository.deleteById("1")).thenReturn(Mono.empty());

        StepVerifier.create(flightService.deleteFlight("1"))
                .verifyComplete();

        verify(flightRepository, times(1)).deleteById("1");
    }

    @Test
    void testSearchFlights() {
        FlightSearchRequest request = new FlightSearchRequest();
        request.setFromPlace("Delhi");
        request.setToPlace("Mumbai");
        request.setDepartureDate(LocalDate.now());

        when(flightRepository.findByFromPlaceAndToPlaceAndDepartureDateTimeBetween(
                anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Flux.just(testFlight));

        StepVerifier.create(flightService.searchFlights(request))
                .expectNext(testFlight)
                .verifyComplete();

        verify(flightRepository, times(1))
                .findByFromPlaceAndToPlaceAndDepartureDateTimeBetween(
                        anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testSearchFlightsEmpty() {
        FlightSearchRequest request = new FlightSearchRequest();
        request.setFromPlace("CityA");
        request.setToPlace("CityB");
        request.setDepartureDate(LocalDate.now());

        when(flightRepository.findByFromPlaceAndToPlaceAndDepartureDateTimeBetween(
                anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Flux.empty());

        StepVerifier.create(flightService.searchFlights(request))
                .verifyComplete();

        verify(flightRepository, times(1))
                .findByFromPlaceAndToPlaceAndDepartureDateTimeBetween(
                        anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
    }
}

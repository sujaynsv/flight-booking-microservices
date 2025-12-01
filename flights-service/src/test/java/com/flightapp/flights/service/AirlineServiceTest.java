package com.flightapp.flights.service;

import com.flightapp.flights.model.Airline;
import com.flightapp.flights.repository.AirlineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AirlineServiceTest {

    @Mock
    private AirlineRepository airlineRepository;

    @InjectMocks
    private AirlineService airlineService;

    private Airline testAirline;

    @BeforeEach
    void setUp() {
        testAirline = new Airline();
        testAirline.setId("airline123");
        testAirline.setAirlineName("IndiGo");
        testAirline.setAirlineCode("6E");
    }

    @Test
    void testCreateAirline() {
        when(airlineRepository.save(any(Airline.class))).thenReturn(Mono.just(testAirline));

        StepVerifier.create(airlineService.createAirline(testAirline))
                .expectNext(testAirline)
                .verifyComplete();

        verify(airlineRepository, times(1)).save(any(Airline.class));
    }

    @Test
    void testGetAllAirlines() {
        when(airlineRepository.findAll()).thenReturn(Flux.just(testAirline));

        StepVerifier.create(airlineService.getAllAirlines())
                .expectNext(testAirline)
                .verifyComplete();

        verify(airlineRepository, times(1)).findAll();
    }

    @Test
    void testGetAirlineById() {
        when(airlineRepository.findById("airline123")).thenReturn(Mono.just(testAirline));

        StepVerifier.create(airlineService.getAirlineById("airline123"))
                .expectNext(testAirline)
                .verifyComplete();

        verify(airlineRepository, times(1)).findById("airline123");
    }

    @Test
    void testGetAirlineById_NotFound() {
        when(airlineRepository.findById("missing")).thenReturn(Mono.empty());

        StepVerifier.create(airlineService.getAirlineById("missing"))
                .expectErrorMatches(ex ->
                        ex instanceof RuntimeException &&
                        ex.getMessage().contains("Airline not found with ID: missing"))
                .verify();

        verify(airlineRepository, times(1)).findById("missing");
    }

    @Test
    void testDeleteAirline() {
        when(airlineRepository.deleteById("airline123")).thenReturn(Mono.empty());

        StepVerifier.create(airlineService.deleteAirline("airline123"))
                .verifyComplete();

        verify(airlineRepository, times(1)).deleteById("airline123");
    }

    @Test
    void testUpdateAirline() {
        Airline existing = new Airline();
        existing.setId("airline123");
        existing.setAirlineName("Old");
        existing.setAirlineCode("OL");

        Airline updated = new Airline();
        updated.setAirlineName("NewName");
        updated.setAirlineCode("NN");

        when(airlineRepository.findById("airline123")).thenReturn(Mono.just(existing));
        when(airlineRepository.save(any(Airline.class)))
                .thenAnswer(invocation -> {
                    Airline a = invocation.getArgument(0, Airline.class);
                    return Mono.just(a);
                });

        StepVerifier.create(airlineService.updateAirline("airline123", updated))
            .assertNext(result -> {
                assertEquals("NewName", result.getAirlineName());
                assertEquals("NN", result.getAirlineCode());
            })
            .verifyComplete();

        verify(airlineRepository, times(1)).findById("airline123");
        verify(airlineRepository, times(1)).save(any(Airline.class));
    }

    @Test
    void testUpdateAirline_NotFound() {
        Airline updated = new Airline();
        when(airlineRepository.findById("missing")).thenReturn(Mono.empty());

        StepVerifier.create(airlineService.updateAirline("missing", updated))
                .expectErrorMatches(ex ->
                        ex instanceof RuntimeException &&
                        ex.getMessage().contains("Airline not found with ID: missing"))
                .verify();

        verify(airlineRepository, times(1)).findById("missing");
    }
}

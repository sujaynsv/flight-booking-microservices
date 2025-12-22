//package com.flightapp.flights.controller;
//
//import com.flightapp.flights.dto.FlightSearchRequest;
//import com.flightapp.flights.model.Flight;
//import com.flightapp.flights.service.FlightService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyInt;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class FlightControllerTest {
//
//    @Mock
//    private FlightService flightService;
//
//    @InjectMocks
//    private FlightController flightController;
//
//    private Flight testFlight;
//
//    @BeforeEach
//    void setUp() {
//        testFlight = new Flight();
//        testFlight.setId("flight123");
//        testFlight.setFlightNumber("6E123");
//        testFlight.setFromPlace("Delhi");
//        testFlight.setToPlace("Mumbai");
//    }
//
//    @Test
//    void testAddFlight() {
//        when(flightService.addFlight(any(Flight.class))).thenReturn(Mono.just(testFlight));
//
//        StepVerifier.create(flightController.addFlight(testFlight))
//                .assertNext(response -> {
//                    assertNotNull(response);
////                    assertEquals(200, response.getStatusCodeValue());
//                    assertNotNull(response.getBody());
//                    assertEquals("flight123", response.getBody().get("flightId"));
//                })
//                .verifyComplete();
//
//        verify(flightService, times(1)).addFlight(any(Flight.class));
//    }
//
//    @Test
//    void testSearchFlights() {
//        FlightSearchRequest request = new FlightSearchRequest();
//        when(flightService.searchFlights(any(FlightSearchRequest.class)))
//                .thenReturn(Flux.just(testFlight));
//
//        StepVerifier.create(flightController.searchFlights(request))
//                .expectNext(testFlight)
//                .verifyComplete();
//
//        verify(flightService, times(1)).searchFlights(any(FlightSearchRequest.class));
//    }
//
//    @Test
//    void testGetFlightById() {
//        when(flightService.getFlightById("flight123")).thenReturn(Mono.just(testFlight));
//
//        StepVerifier.create(flightController.getFlightById("flight123"))
//                .expectNext(testFlight)
//                .verifyComplete();
//
//        verify(flightService, times(1)).getFlightById("flight123");
//    }
//
//    @Test
//    void testUpdateSeats() {
//        when(flightService.updateSeats(anyString(), anyInt())).thenReturn(Mono.just(testFlight));
//
//        StepVerifier.create(flightController.updateSeats("flight123", 10))
//                .assertNext(response -> {
//                    assertNotNull(response);
////                    assertEquals(200, response.getStatusCodeValue());
//                    assertNotNull(response.getBody());
//                    assertEquals("flight123", response.getBody().get("flightId"));
//                })
//                .verifyComplete();
//
//        verify(flightService, times(1)).updateSeats("flight123", 10);
//    }
//
//    @Test
//    void testDeleteFlight() {
//        when(flightService.deleteFlight("flight123")).thenReturn(Mono.empty());
//
//        StepVerifier.create(flightController.deleteFlight("flight123"))
//                .assertNext(response -> {
//                    assertNotNull(response);
////                    assertEquals(200, response.getStatusCodeValue());
//                })
//                .verifyComplete();
//
//        verify(flightService, times(1)).deleteFlight("flight123");
//    }
//}

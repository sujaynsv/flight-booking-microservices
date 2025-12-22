//package com.flightapp.flights.controller;
//
//import com.flightapp.flights.model.Airline;
//import com.flightapp.flights.service.AirlineService;
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
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class AirlineControllerTest {
//
//    @Mock
//    private AirlineService airlineService;
//
//    @InjectMocks
//    private AirlineController airlineController;
//
//    private Airline testAirline;
//
//    @BeforeEach
//    void setUp() {
//        testAirline = new Airline();
//        testAirline.setId("airline123");
//        testAirline.setAirlineName("IndiGo");
//        testAirline.setAirlineCode("6E");
//    }
//
//    @Test
//    void testCreateAirline() {
//        when(airlineService.createAirline(any(Airline.class))).thenReturn(Mono.just(testAirline));
//
//        StepVerifier.create(airlineController.createAirline(testAirline))
//                .assertNext(response -> {
//                    assertNotNull(response);
////                    assertEquals(200, response.getStatusCodeValue());
//                    assertNotNull(response.getBody());
//                    assertEquals("airline123", response.getBody().get("airlineId"));
//                })
//                .verifyComplete();
//
//        verify(airlineService, times(1)).createAirline(any(Airline.class));
//    }
//
//    @Test
//    void testGetAllAirlines() {
//        when(airlineService.getAllAirlines()).thenReturn(Flux.just(testAirline));
//
//        StepVerifier.create(airlineController.getAllAirlines())
//                .expectNext(testAirline)
//                .verifyComplete();
//
//        verify(airlineService, times(1)).getAllAirlines();
//    }
//
//    @Test
//    void testGetAirlineById() {
//        when(airlineService.getAirlineById("airline123")).thenReturn(Mono.just(testAirline));
//
//        StepVerifier.create(airlineController.getAirlineById("airline123"))
//                .expectNext(testAirline)
//                .verifyComplete();
//
//        verify(airlineService, times(1)).getAirlineById("airline123");
//    }
//
//    @Test
//    void testUpdateAirline() {
//        when(airlineService.updateAirline(anyString(), any(Airline.class)))
//                .thenReturn(Mono.just(testAirline));
//
//        StepVerifier.create(airlineController.updateAirline("airline123", testAirline))
//                .assertNext(response -> {
//                    assertNotNull(response);
////                    assertEquals(200, response.getStatusCodeValue());
//                    assertNotNull(response.getBody());
//                    assertEquals("airline123", response.getBody().get("airlineId"));
//                })
//                .verifyComplete();
//
//        verify(airlineService, times(1)).updateAirline("airline123", testAirline);
//    }
//
//    @Test
//    void testDeleteAirline() {
//        when(airlineService.deleteAirline("airline123")).thenReturn(Mono.empty());
//
//        StepVerifier.create(airlineController.deleteAirline("airline123"))
//                .assertNext(response -> {
//                    assertNotNull(response);
////                    assertEquals(200, response.getStatusCodeValue());
//                })
//                .verifyComplete();
//
//        verify(airlineService, times(1)).deleteAirline("airline123");
//    }
//}

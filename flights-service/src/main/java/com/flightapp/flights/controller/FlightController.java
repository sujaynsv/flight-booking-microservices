package com.flightapp.flights.controller;

import com.flightapp.flights.dto.FlightSearchRequest;
import com.flightapp.flights.model.Flight;
import com.flightapp.flights.service.FlightService;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/flight")
public class FlightController {
    
    private final FlightService flightService;
    private static final Logger log = LoggerFactory.getLogger(FlightService.class);
    
    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }
    
    @PostMapping("/airline/inventory")
    public Mono<ResponseEntity<Map<String, String>>> addFlight(
            @RequestHeader("X-User-Role") String role,
            @RequestBody Flight flight) {
        
        if (!"ADMIN".equals(role)) {
            return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Forbidden: Admin access required")));
        }
        
        return flightService.addFlight(flight)
                .map(saved -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(Map.of("flightId", saved.getId())));
    }
    
    @PostMapping("/search")
    public Flux<Flight> searchFlights(@RequestBody FlightSearchRequest searchRequest) {
        return flightService.searchFlights(searchRequest);
    }
    
    @GetMapping("/{id}")
    public Mono<Flight> getFlightById(@PathVariable String id) {
        return flightService.getFlightById(id);
    }
    
    @PutMapping("/{id}/seats")
    public Mono<ResponseEntity<Map<String, String>>> updateSeats(
            @PathVariable String id, 
            @RequestParam Integer seats) {
        return flightService.updateSeats(id, seats)
                .map(updated -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(Map.of("flightId", updated.getId())));
    }
    
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteFlight(@PathVariable String id) {
        return flightService.deleteFlight(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
    
    
    @PutMapping("/{flightId}/release-seats")
    public Mono<Void> releaseSeats(
            @PathVariable String flightId,
            @RequestParam Integer seats) {

        return flightService.releaseSeats(flightId, seats);
    }

    @GetMapping("/inventory/{flightId}/booked-seats")
    public Mono<ResponseEntity<Map<String, Object>>> getBookedSeats(@PathVariable String flightId) {
        log.info("Request to get booked seats for flight: {}", flightId);
        
        return flightService.getBookedSeats(flightId)
                .map(bookedSeats -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("bookedSeats", bookedSeats);
                    log.info("Returning {} booked seats for flight: {}", bookedSeats.size(), flightId);
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(error -> {
                    log.error("Error fetching booked seats: {}", error.getMessage());
                    return Mono.just(ResponseEntity.ok(Map.of("bookedSeats", List.of())));
                })
                .defaultIfEmpty(ResponseEntity.ok(Map.of("bookedSeats", List.of())));
    }

}

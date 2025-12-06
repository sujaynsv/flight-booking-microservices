package com.flightapp.flights.controller;

import com.flightapp.flights.dto.FlightSearchRequest;
import com.flightapp.flights.model.Flight;
import com.flightapp.flights.service.FlightService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/flight")
public class FlightController {
    
    private final FlightService flightService;
    
    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }
    
    @PostMapping("/airline/inventory")
    public Mono<ResponseEntity<Map<String, String>>> addFlight(@RequestBody Flight flight) {
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
}

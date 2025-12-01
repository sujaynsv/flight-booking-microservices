package com.flightapp.flights.controller;

import com.flightapp.flights.model.Airline;
import com.flightapp.flights.service.AirlineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/airline")
public class AirlineController {
    
    private final AirlineService airlineService;
    
    public AirlineController(AirlineService airlineService) {
        this.airlineService = airlineService;
    }
    
    @PostMapping
    public Mono<ResponseEntity<Map<String, String>>> createAirline(@RequestBody Airline airline) {
        return airlineService.createAirline(airline)
                .map(saved -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(Map.of("airlineId", saved.getId())));
    }
    
    @GetMapping
    public Flux<Airline> getAllAirlines() {
        return airlineService.getAllAirlines();
    }
    
    @GetMapping("/{id}")
    public Mono<Airline> getAirlineById(@PathVariable String id) {
        return airlineService.getAirlineById(id);
    }
    
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Map<String, String>>> updateAirline(
            @PathVariable String id, 
            @RequestBody Airline airline) {
        return airlineService.updateAirline(id, airline)
                .map(updated -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(Map.of("airlineId", updated.getId())));
    }
    
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAirline(@PathVariable String id) {
        return airlineService.deleteAirline(id)
                .then(Mono.just(ResponseEntity.ok().<Void>build()));
    }
}

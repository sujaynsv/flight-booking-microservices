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
    public Mono<ResponseEntity<Map<String, String>>> createAirline(
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @RequestBody Airline airline) {

        System.out.println("createAirline X-User-Role = " + role);  // debug log

        if (!"ADMIN".equals(role)) {
            return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Forbidden: Admin access required")));
        }

        return airlineService.createAirline(airline)
                .map(saved -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(Map.of("airlineId", saved.getId())));
    }

    @GetMapping
    public Flux<Airline> getAllAirlines(
            @RequestHeader(value = "X-User-Role", required = false) String role) {

        System.out.println("AirlineController X-User-Role = " + role);  // debug log

        if (!"ADMIN".equals(role)) {
            return Flux.error(new RuntimeException("Forbidden: Admin access required"));
        }
        return airlineService.getAllAirlines();
    }

    
    @GetMapping("/{id}")
    public Mono<Airline> getAirlineById(
            @RequestHeader("X-User-Role") String role,
            @PathVariable String id) {
        if (!"ADMIN".equals(role)) {
            return Mono.error(new RuntimeException("Forbidden: Admin access required"));
        }
        return airlineService.getAirlineById(id);
    }
    
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Map<String, String>>> updateAirline(
            @RequestHeader("X-User-Role") String role,
            @PathVariable String id, 
            @RequestBody Airline airline) {
        
        if (!"ADMIN".equals(role)) {
            return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Forbidden: Admin access required")));
        }
        
        return airlineService.updateAirline(id, airline)
                .map(updated -> ResponseEntity
                        .status(HttpStatus.OK)
                        .body(Map.of("airlineId", updated.getId())));
    }
    
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteAirline(
            @RequestHeader("X-User-Role") String role,
            @PathVariable String id) {
        
        if (!"ADMIN".equals(role)) {
            return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).<Void>build());
        }
        
        return airlineService.deleteAirline(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
}

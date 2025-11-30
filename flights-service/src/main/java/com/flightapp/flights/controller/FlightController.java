package com.flightapp.flights.controller;

import com.flightapp.flights.dto.FlightSearchRequest;
import com.flightapp.flights.model.Flight;
import com.flightapp.flights.service.FlightService;
// import jakarta.validation.Valid;  // COMMENT THIS OUT
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1.0/flight")
public class FlightController {
    
    private final FlightService flightService;
    
    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }
    
    @PostMapping("/airline/inventory")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Flight> addFlight(@RequestBody Flight flight) {  // REMOVED @Valid
        System.out.println("========== CONTROLLER HIT! ==========");
        System.out.println("Received flight: " + flight);
        return flightService.addFlight(flight);
    }
    
    @PostMapping("/search")
    public Flux<Flight> searchFlights(@RequestBody FlightSearchRequest searchRequest) {  // REMOVED @Valid
        return flightService.searchFlights(searchRequest);
    }
    
    @GetMapping("/airline/{id}")
    public Mono<Flight> getFlightById(@PathVariable String id) {
        return flightService.getFlightById(id);
    }
    
    @PutMapping("/airline/{id}/seats")
    public Mono<Flight> updateSeats(@PathVariable String id, 
                                     @RequestParam Integer seatsToBook) {
        return flightService.updateAvailableSeats(id, seatsToBook);
    }
}

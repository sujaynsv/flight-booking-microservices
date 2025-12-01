package com.flightapp.flights.service;

import com.flightapp.flights.dto.FlightSearchRequest;
import com.flightapp.flights.model.Flight;
import com.flightapp.flights.repository.FlightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class FlightService {
    
    private static final Logger log = LoggerFactory.getLogger(FlightService.class);
    
    private final FlightRepository flightRepository;
    
    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }
    
    public Mono<Flight> addFlight(Flight flight) {
        log.info("Adding new flight: {}", flight.getFlightNumber());
        flight.setCreatedAt(LocalDateTime.now());
        flight.setUpdatedAt(LocalDateTime.now());
        flight.setAvailableSeats(flight.getTotalSeats());
        return flightRepository.save(flight);
    }
    
    public Flux<Flight> searchFlights(FlightSearchRequest searchRequest) {
        log.info("Searching flights from {} to {} on {}", 
                searchRequest.getFromPlace(), 
                searchRequest.getToPlace(), 
                searchRequest.getDepartureDate());
        
        return flightRepository.findByFromPlaceAndToPlaceAndDepartureDateTimeBetween(
                searchRequest.getFromPlace(),
                searchRequest.getToPlace(),
                searchRequest.getDepartureDate().atStartOfDay(),
                searchRequest.getDepartureDate().atTime(23, 59, 59)
        );
    }
    
    public Mono<Flight> getFlightById(String id) {
        log.info("Fetching flight with ID: {}", id);
        return flightRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Flight not found with ID: " + id)));
    }
    
    public Mono<Flight> updateSeats(String id, Integer seatsToBook) {
        log.info("Updating seats for flight: {}", id);
        
        return flightRepository.findById(id)
                .flatMap(flight -> {
                    if (flight.getAvailableSeats() < seatsToBook) {
                        return Mono.error(new RuntimeException(
                                "Not enough seats available. Available: " + flight.getAvailableSeats()));
                    }
                    
                    flight.setAvailableSeats(flight.getAvailableSeats() - seatsToBook);
                    flight.setUpdatedAt(LocalDateTime.now());
                    return flightRepository.save(flight);
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Flight not found with ID: " + id)));
    }
    
    public Mono<Void> deleteFlight(String id) {
        log.info("Deleting flight with ID: {}", id);
        return flightRepository.deleteById(id);
    }
    
    public Flux<Flight> getAllFlights() {
        return flightRepository.findAll();
    }
}

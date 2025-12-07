package com.flightapp.flights.service;

import com.flightapp.flights.dto.FlightSearchRequest;
import com.flightapp.flights.exception.FlightNotFoundException;
import com.flightapp.flights.model.Flight;
import com.flightapp.flights.repository.FlightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FlightService {
    
    private static final Logger log = LoggerFactory.getLogger(FlightService.class);
    
    private final FlightRepository flightRepository;
    
    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public Mono<Flight> addFlight(Flight flight) {
        log.info("Adding new flight");
        return flightRepository.save(flight)
                .doOnSuccess(f -> log.info("Flight added and cached: {}", f.getId()));
    }
    
    public Flux<Flight> searchFlights(FlightSearchRequest searchRequest) {
        log.info("Searching flights from {} to {}", searchRequest.getFromPlace(), searchRequest.getToPlace());
        
        return flightRepository.findAll()
                .filter(flight -> 
                    flight.getFromPlace().equalsIgnoreCase(searchRequest.getFromPlace()) &&
                    flight.getToPlace().equalsIgnoreCase(searchRequest.getToPlace()) &&
                    flight.getDepartureDateTime().toLocalDate().isEqual(searchRequest.getDepartureDate())
                )
                .doOnComplete(() -> log.info("Flight search completed"));
    }

    public Mono<Flight> getFlightById(String id) {
        log.info("CACHE MISS - Fetching flight from MongoDB: {}", id);
        return flightRepository.findById(id)
                .switchIfEmpty(Mono.error(new FlightNotFoundException("Flight not found with id: " + id)))
                .doOnSuccess(flight -> log.info("Flight fetched and cached: {}", id));
    }
    

    public Mono<Flight> updateSeats(String id, Integer seats) {
        log.info("Updating seats for flight: {}, seats: {}", id, seats);
        
        return flightRepository.findById(id)
                .switchIfEmpty(Mono.error(new FlightNotFoundException("Flight not found with id: " + id)))
                .flatMap(flight -> {
                    int newAvailableSeats = flight.getAvailableSeats() - seats;
                    
                    if (newAvailableSeats < 0) {
                        return Mono.error(new RuntimeException("Not enough seats available"));
                    }
                    
                    flight.setAvailableSeats(newAvailableSeats);
                    return flightRepository.save(flight);
                })
                .doOnSuccess(f -> log.info("Flight seats updated and cache refreshed: {}", id));
    }
    

    public Mono<Void> deleteFlight(String id) {
        log.info("Deleting flight and evicting cache: {}", id);
        
        return flightRepository.findById(id)
                .switchIfEmpty(Mono.error(new FlightNotFoundException("Flight not found with id: " + id)))
                .flatMap(flight -> flightRepository.deleteById(id))
                .doOnSuccess(v -> log.info("Flight deleted and cache cleared: {}", id));
    }
}

package com.flightapp.flights.service;

import com.flightapp.flights.model.Airline;
import com.flightapp.flights.repository.AirlineRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class AirlineService {
    
    private static final Logger log = LoggerFactory.getLogger(AirlineService.class);
    
    private final AirlineRepository airlineRepository;
    
    public AirlineService(AirlineRepository airlineRepository) {
        this.airlineRepository = airlineRepository;
    }
    
    public Mono<Airline> createAirline(Airline airline) {
        log.info("Creating airline: {}", airline.getAirlineName());
        airline.setCreatedAt(LocalDateTime.now());
        airline.setUpdatedAt(LocalDateTime.now());
        return airlineRepository.save(airline);
    }
    
    public Flux<Airline> getAllAirlines() {
        return airlineRepository.findAll();
    }
    
    public Mono<Airline> getAirlineById(String id) {
        return airlineRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Airline not found with ID: " + id)));
    }
    
    public Mono<Airline> updateAirline(String id, Airline airline) {
        return airlineRepository.findById(id)
                .flatMap(existing -> {
                    existing.setAirlineName(airline.getAirlineName());
                    existing.setAirlineCode(airline.getAirlineCode());
                    existing.setContactEmail(airline.getContactEmail());
                    existing.setContactPhone(airline.getContactPhone());
                    existing.setUpdatedAt(LocalDateTime.now());
                    return airlineRepository.save(existing);
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Airline not found with ID: " + id)));
    }
    
    public Mono<Void> deleteAirline(String id) {
        return airlineRepository.deleteById(id);
    }
}

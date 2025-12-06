package com.flightapp.flights.service;

import com.flightapp.flights.exception.AirlineNotFoundException;
import com.flightapp.flights.model.Airline;
import com.flightapp.flights.repository.AirlineRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AirlineService {
    
    private static final Logger log = LoggerFactory.getLogger(AirlineService.class);
    
    private final AirlineRepository airlineRepository;
    
    public AirlineService(AirlineRepository airlineRepository) {
        this.airlineRepository = airlineRepository;
    }
    
    @CacheEvict(value = "airlines", allEntries = true)
    public Mono<Airline> createAirline(Airline airline) {
        log.info("Creating airline and clearing cache");
        return airlineRepository.save(airline)
                .doOnSuccess(a -> log.info("Airline created: {}", a.getId()));
    }
    
    @Cacheable(value = "airlines", key = "'all'")
    public Flux<Airline> getAllAirlines() {
        log.info("CACHE MISS - Fetching all airlines from MongoDB");
        return airlineRepository.findAll()
                .doOnComplete(() -> log.info("All airlines fetched and cached"));
    }
    
    @Cacheable(value = "airlines", key = "#id")
    public Mono<Airline> getAirlineById(String id) {
        log.info("CACHE MISS - Fetching airline from MongoDB: {}", id);
        return airlineRepository.findById(id)
                .switchIfEmpty(Mono.error(new AirlineNotFoundException("Airline not found with id: " + id)))
                .doOnSuccess(airline -> log.info("Airline fetched and cached: {}", id));
    }
    
    @CacheEvict(value = "airlines", allEntries = true)
    public Mono<Airline> updateAirline(String id, Airline airline) {
        log.info("Updating airline and clearing cache: {}", id);
        
        return airlineRepository.findById(id)
                .switchIfEmpty(Mono.error(new AirlineNotFoundException("Airline not found with id: " + id)))
                .flatMap(existing -> {
                    existing.setAirlineName(airline.getAirlineName());
                    existing.setAirlineCode(airline.getAirlineCode());
                    return airlineRepository.save(existing);
                })
                .doOnSuccess(a -> log.info("Airline updated: {}", id));
    }
    
    @CacheEvict(value = "airlines", allEntries = true)
    public Mono<Void> deleteAirline(String id) {
        log.info("Deleting airline and clearing cache: {}", id);
        
        return airlineRepository.findById(id)
                .switchIfEmpty(Mono.error(new AirlineNotFoundException("Airline not found with id: " + id)))
                .flatMap(airline -> airlineRepository.deleteById(id))
                .doOnSuccess(v -> log.info("Airline deleted: {}", id));
    }
}

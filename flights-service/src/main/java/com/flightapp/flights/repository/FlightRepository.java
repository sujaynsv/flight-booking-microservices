package com.flightapp.flights.repository;

import com.flightapp.flights.model.Flight;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

public interface FlightRepository extends ReactiveMongoRepository<Flight, String> {
    
    Flux<Flight> findByFromPlaceAndToPlaceAndDepartureDateTimeBetween(
            String fromPlace, 
            String toPlace, 
            LocalDateTime startDateTime, 
            LocalDateTime endDateTime
    );
    
    Flux<Flight> findByAirlineId(String airlineId);
}

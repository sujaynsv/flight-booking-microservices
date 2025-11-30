package com.flightapp.flights.repository;

import com.flightapp.flights.model.Flight;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

@Repository
public interface FlightRepository extends ReactiveMongoRepository<Flight, String> {
    Flux<Flight> findByFromPlaceAndToPlaceAndDepartureDateTimeBetween(
        String fromPlace, 
        String toPlace, 
        LocalDateTime startDate, 
        LocalDateTime endDate
    );
}

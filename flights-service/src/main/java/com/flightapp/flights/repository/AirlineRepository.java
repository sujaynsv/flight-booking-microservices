package com.flightapp.flights.repository;

import com.flightapp.flights.model.Airline;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface AirlineRepository extends ReactiveMongoRepository<Airline, String> {
    Mono<Airline> findByAirlineCode(String airlineCode);
}

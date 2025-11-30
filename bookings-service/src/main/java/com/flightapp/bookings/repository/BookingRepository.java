package com.flightapp.bookings.repository;

import com.flightapp.bookings.model.Booking;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BookingRepository extends ReactiveMongoRepository<Booking, String> {
    Mono<Booking> findByPnr(String pnr);
    Flux<Booking> findByEmail(String email);
}

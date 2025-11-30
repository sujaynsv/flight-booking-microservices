package com.flightapp.flights.service;

import com.flightapp.flights.dto.FlightSearchRequest;
import com.flightapp.flights.exception.FlightNotFoundException;
import com.flightapp.flights.exception.InsufficientSeatsException;
import com.flightapp.flights.model.Flight;
import com.flightapp.flights.repository.FlightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class FlightService {
    
    private static final Logger log = LoggerFactory.getLogger(FlightService.class);
    
    private final FlightRepository flightRepository;
    
    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }
    
    public Mono<Flight> addFlight(Flight flight) {
        if (flight.getBookedSeats() == null) {
            flight.setBookedSeats(new ArrayList<>());
        }
        flight.setAvailableSeats(flight.getTotalSeats());
        return flightRepository.save(flight)
                .doOnSuccess(f -> log.info("Flight added successfully with ID: {}", f.getId()));
    }
    
    public Flux<Flight> searchFlights(FlightSearchRequest request) {
        LocalDateTime startDate = request.getDepartureDate().atStartOfDay();
        LocalDateTime endDate = startDate.plusDays(1);
        
        return flightRepository.findByFromPlaceAndToPlaceAndDepartureDateTimeBetween(
                request.getFromPlace(),
                request.getToPlace(),
                startDate,
                endDate
        ).filter(flight -> flight.getAvailableSeats() > 0)
         .doOnComplete(() -> log.info("Flight search completed for {} to {}", 
                 request.getFromPlace(), request.getToPlace()));
    }
    
    public Mono<Flight> getFlightById(String id) {
        return flightRepository.findById(id)
                .switchIfEmpty(Mono.error(new FlightNotFoundException("Flight not found with ID: " + id)));
    }
    
    public Mono<Flight> updateAvailableSeats(String flightId, Integer seatsToBook) {
        return flightRepository.findById(flightId)
                .switchIfEmpty(Mono.error(new FlightNotFoundException("Flight not found with ID: " + flightId)))
                .flatMap(flight -> {
                    if (flight.getAvailableSeats() < seatsToBook) {
                        return Mono.error(new InsufficientSeatsException(
                                "Not enough seats available. Available: " + flight.getAvailableSeats()));
                    }
                    flight.setAvailableSeats(flight.getAvailableSeats() - seatsToBook);
                    return flightRepository.save(flight);
                })
                .doOnSuccess(f -> log.info("Updated seats for flight: {}. New available seats: {}", 
                        flightId, f.getAvailableSeats()));
    }
}

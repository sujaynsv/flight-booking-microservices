package com.flightapp.bookings.client;

import com.flightapp.bookings.dto.FlightDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class FlightsClient {
    
    private static final Logger log = LoggerFactory.getLogger(FlightsClient.class);
    
    private final WebClient.Builder webClientBuilder;
    
    @Value("${flights.service.url}")
    private String flightsServiceUrl;
    
    public FlightsClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }
    
    @CircuitBreaker(name = "flights-service", fallbackMethod = "getFlightFallback")
    public Mono<FlightDTO> getFlightById(String flightId) {
        log.info("Calling flights service to get flight: {}", flightId);
        return webClientBuilder.build()
                .get()
                .uri(flightsServiceUrl + "/api/v1.0/flight/airline/" + flightId)
                .retrieve()
                .bodyToMono(FlightDTO.class)
                .doOnSuccess(flight -> log.info("Successfully fetched flight: {}", flightId))
                .doOnError(error -> log.error("Error fetching flight: {}", error.getMessage()));
    }
    
    @CircuitBreaker(name = "flights-service", fallbackMethod = "updateSeatsFallback")
    public Mono<FlightDTO> updateSeats(String flightId, Integer seats) {
        log.info("Calling flights service to update {} seats for flight: {}", seats, flightId);
        return webClientBuilder.build()
                .put()
                .uri(flightsServiceUrl + "/api/v1.0/flight/airline/" + flightId + "/seats?seatsToBook=" + seats)
                .retrieve()
                .bodyToMono(FlightDTO.class)
                .doOnSuccess(flight -> log.info("Successfully updated seats for flight: {}", flightId))
                .doOnError(error -> log.error("Error updating seats: {}", error.getMessage()));
    }
    
    private Mono<FlightDTO> getFlightFallback(String flightId, Exception ex) {
        log.error("Circuit breaker activated for getFlight. Flight ID: {}, Error: {}", flightId, ex.getMessage());
        return Mono.error(new RuntimeException("Flights service is currently unavailable. Please try again later."));
    }
    
    private Mono<FlightDTO> updateSeatsFallback(String flightId, Integer seats, Exception ex) {
        log.error("Circuit breaker activated for updateSeats. Flight ID: {}, Error: {}", flightId, ex.getMessage());
        return Mono.error(new RuntimeException("Unable to update seats - flights service is unavailable."));
    }
}

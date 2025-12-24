package com.flightapp.bookings.client;

import com.flightapp.bookings.dto.FlightDTO;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
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
    
    public Mono<FlightDTO> getFlightById(String flightId) {
        log.info("Calling flights service to get flight: {}", flightId);
        
        return webClientBuilder.build()
                .get()
                .uri(flightsServiceUrl + "/flight/" + flightId)
                .retrieve()
                .bodyToMono(FlightDTO.class)
                .doOnSuccess(flight -> log.info("Successfully fetched flight: {}", flightId))
                .doOnError(error -> log.error("Error fetching flight: {}", error.getMessage()));
    }
    
    public Mono<FlightDTO> updateSeats(String flightId, Integer seats) {
        log.info("Calling flights service to update {} seats for flight: {}", seats, flightId);
        
        return webClientBuilder.build()
                .put()
                .uri(flightsServiceUrl + "/flight/" + flightId + "/seats?seats=" + seats)
                .retrieve()
                .bodyToMono(FlightDTO.class)
                .doOnSuccess(flight -> log.info("Successfully updated seats for flight: {}", flightId))
                .doOnError(error -> log.error("Error updating seats: {}", error.getMessage()));
    }
    
    public Mono<Void> releaseSeats(String flightId, Integer seats) {
        String url = flightsServiceUrl
                + "/flight/"
                + flightId
                + "/release-seats?seats="
                + seats;

        log.info("Calling flights service to release seats. URL: {}", url);

        return webClientBuilder.build()
                .put()
                .uri(url)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v ->
                        log.info("Successfully released {} seats for flight {}", seats, flightId)
                )
                .doOnError(error ->
                        log.error("Error releasing seats: {}", error.getMessage())
                );
    }

        public Mono<List<String>> getBookedSeats(String flightId) {
        log.info("Calling flights service to get booked seats for flight: {}", flightId);
        
        return webClientBuilder.build()
                .get()
                .uri(flightsServiceUrl + "/flight/booked-seats/" + flightId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .doOnSuccess(seats -> log.info("Successfully fetched {} booked seats for flight: {}", 
                    seats.size(), flightId))
                .doOnError(error -> log.error("Error fetching booked seats: {}", error.getMessage()))
                .onErrorReturn(List.of()); // Return empty list on error
    }

}